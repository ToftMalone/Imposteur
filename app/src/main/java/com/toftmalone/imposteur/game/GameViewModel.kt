package com.toftmalone.imposteur.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toftmalone.imposteur.BuildConfig
import com.toftmalone.imposteur.data.ApkInstaller
import com.toftmalone.imposteur.data.ApkVerifier
import com.toftmalone.imposteur.data.AppVersion
import com.toftmalone.imposteur.data.AvailableUpdate
import com.toftmalone.imposteur.data.Avatars
import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.ImposteurRepository
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.data.ReleaseNotes
import com.toftmalone.imposteur.data.Round
import com.toftmalone.imposteur.data.UpdateCheckOutcome
import com.toftmalone.imposteur.data.UpdateChecker
import com.toftmalone.imposteur.data.UpdateDownload
import com.toftmalone.imposteur.data.UpdateDownloadException
import com.toftmalone.imposteur.data.UpdateDownloader
import com.toftmalone.imposteur.data.RoundOutcome
import com.toftmalone.imposteur.data.WhatsNew
import com.toftmalone.imposteur.data.WordPack
import com.toftmalone.imposteur.data.WordPacks
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Where the group currently is inside a round. */
enum class GamePhase {
    /** Not playing: the setup screen owns the flow. */
    IDLE,

    /** Passing the phone around to read the secret cards. */
    REVEAL,

    /** Everyone describes the word out loud. */
    DISCUSSION,

    /** Choosing who gets voted out. */
    VOTE,

    /** Showing who was voted out and what they were. */
    ELIMINATION,

    /** A caught impostor gets one shot at naming the word. */
    IMPOSTER_GUESS,

    /** The round is over; scores have been applied. */
    ROUND_END,
}

data class GameUiState(
    val players: List<Player> = emptyList(),
    val settings: GameSettings = GameSettings(),
    val phase: GamePhase = GamePhase.IDLE,
    val round: Round? = null,
    val roundNumber: Int = 0,
    val revealIndex: Int = 0,
    val alivePlayerIds: List<String> = emptyList(),
    val lastElimination: EliminationResult? = null,
    val outcome: RoundOutcome? = null,
    val guessWasCorrect: Boolean? = null,
    val error: GameError? = null,
    /** Set once GitHub reports a release newer than this build. */
    val availableUpdate: AvailableUpdate? = null,
    /** Result of the most recent check, for the settings screen to report. */
    val updateCheckOutcome: UpdateCheckOutcome? = null,
    val checkingForUpdate: Boolean = false,
    /** The update window (what's new, then download and install) is open. */
    val showUpdateDialog: Boolean = false,
    val updateDownload: UpdateDownload = UpdateDownload.Idle,
    /** Notes shown once after the app has been updated, or when asked for. */
    val whatsNew: WhatsNew? = null,
) {
    val allPacks: List<WordPack> get() = WordPacks.BUILT_IN

    val selectedPackCount: Int
        get() = allPacks.count { it.id in settings.selectedPackIds }

    val selectedWordCount: Int
        get() = allPacks.filter { it.id in settings.selectedPackIds }.sumOf { it.wordCount }

    val canStart: Boolean
        get() = GameEngine.validate(players, settings, allPacks) == null

    fun playerById(id: String): Player? = players.firstOrNull { it.id == id }

    val alivePlayers: List<Player>
        get() = alivePlayerIds.mapNotNull(::playerById)

    /** The player whose card is on screen during the reveal phase. */
    val revealPlayer: Player?
        get() = round?.speakingOrder?.getOrNull(revealIndex)?.let(::playerById)

    val isLastReveal: Boolean
        get() = round != null && revealIndex >= round.speakingOrder.lastIndex

    /** Speaking order, resolved to players, skipping anyone voted out. */
    val speakingPlayers: List<Player>
        get() = round?.speakingOrder.orEmpty()
            .filter { it in alivePlayerIds }
            .mapNotNull(::playerById)
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImposteurRepository(application)
    private val updateChecker = UpdateChecker(currentVersion = BuildConfig.VERSION_NAME)
    private val updateDownloader = UpdateDownloader(ApkInstaller.downloadDirectory(application))
    private var downloadJob: Job? = null

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.players,
                repository.settings,
            ) { players, settings -> players to settings }
                .collect { (storedPlayers, settings) ->
                    val current = _state.value
                    // Scores are not persisted, so re-apply the ones this
                    // session has earned rather than resetting mid-game.
                    val sessionScores = current.players.associate { it.id to it.score }
                    val merged = storedPlayers.map { it.copy(score = sessionScores[it.id] ?: 0) }
                    _state.value = current.copy(
                        players = merged.ifEmpty { current.players },
                        settings = settings,
                    )
                }
        }
        checkForUpdate(userInitiated = false)
        showWhatsNewAfterUpdate()
        viewModelScope.launch {
            // Seed a starter roster the first time the app is opened.
            if (repository.players.first().isEmpty()) {
                val starter = (0 until 4).map { index -> newPlayer(index) }
                repository.savePlayers(starter)
            }
        }
    }

    // --- updates -------------------------------------------------------------

    /**
     * Asks GitHub whether a newer release exists. Runs once at launch and again
     * whenever the player taps the check in the settings. A failure is silent
     * at launch and reported only when the player asked for it.
     *
     * Finding a new version opens the update window by itself; the in-game
     * screens hold it back until the round is over.
     */
    fun checkForUpdate(userInitiated: Boolean) {
        if (_state.value.checkingForUpdate) return
        _state.update { it.copy(checkingForUpdate = true) }
        viewModelScope.launch {
            if (!userInitiated) updateDownloader.clear()
            val result = updateChecker.check()
            val previous = _state.value.availableUpdate
            val update = when (result.outcome) {
                UpdateCheckOutcome.UPDATE_AVAILABLE -> result.update
                UpdateCheckOutcome.UP_TO_DATE -> null
                // A failed check says nothing new: keep what an earlier one found.
                UpdateCheckOutcome.UNAVAILABLE -> previous
            }
            val sameVersion = update != null && update.versionName == previous?.versionName
            if (!sameVersion) {
                downloadJob?.cancel()
                downloadJob = null
            }
            _state.update { current ->
                current.copy(
                    checkingForUpdate = false,
                    availableUpdate = update,
                    updateDownload = if (sameVersion) current.updateDownload else UpdateDownload.Idle,
                    showUpdateDialog = when {
                        update == null -> false
                        userInitiated || !sameVersion -> true
                        else -> current.showUpdateDialog
                    },
                    updateCheckOutcome = if (userInitiated || result.outcome == UpdateCheckOutcome.UPDATE_AVAILABLE) {
                        result.outcome
                    } else {
                        current.updateCheckOutcome
                    },
                )
            }
        }
    }

    fun openUpdateDialog() {
        if (_state.value.availableUpdate != null) _state.update { it.copy(showUpdateDialog = true) }
    }

    /** "Plus tard": the banner on the home screen stays to come back to it. */
    fun closeUpdateDialog() {
        if (_state.value.updateDownload is UpdateDownload.Running) return
        _state.update { it.copy(showUpdateDialog = false) }
    }

    fun dismissUpdateBanner() {
        if (_state.value.updateDownload is UpdateDownload.Running) return
        _state.update { it.copy(availableUpdate = null, showUpdateDialog = false) }
    }

    /** Downloads the APK of the available release; the UI hands it to the installer once ready. */
    fun startUpdateDownload() {
        val apk = _state.value.availableUpdate?.apk ?: return
        if (downloadJob?.isActive == true) return
        _state.update { it.copy(updateDownload = UpdateDownload.Running(0L, apk.sizeBytes)) }
        downloadJob = viewModelScope.launch {
            val outcome = try {
                val file = updateDownloader.download(apk) { downloaded, total ->
                    _state.update { current ->
                        // A cancelled download may still report once; it must not come back.
                        if (current.updateDownload is UpdateDownload.Running) {
                            current.copy(updateDownload = UpdateDownload.Running(downloaded, total))
                        } else {
                            current
                        }
                    }
                }
                // Arriving intact is not enough: it must be Imposteur, newer, same key.
                val problem = withContext(Dispatchers.IO) {
                    ApkVerifier.problemWith(getApplication<Application>(), file).also { if (it != null) file.delete() }
                }
                if (problem != null) UpdateDownload.Failed(problem) else UpdateDownload.Ready(file)
            } catch (error: CancellationException) {
                throw error
            } catch (error: UpdateDownloadException) {
                UpdateDownload.Failed(error.message ?: DOWNLOAD_FAILED)
            } catch (error: Exception) {
                UpdateDownload.Failed(DOWNLOAD_FAILED)
            }
            _state.update { it.copy(updateDownload = outcome) }
        }
    }

    fun cancelUpdateDownload() {
        downloadJob?.cancel()
        downloadJob = null
        _state.update { it.copy(updateDownload = UpdateDownload.Idle) }
    }

    /** The system installer could not be opened: say so rather than stay silent. */
    fun reportInstallFailure() {
        _state.update {
            it.copy(updateDownload = UpdateDownload.Failed("Impossible d'ouvrir l'installateur d'Android."))
        }
    }

    fun releasesPageUrl(): String = updateChecker.releasesPageUrl()

    // --- what's new ------------------------------------------------------------

    /**
     * On the first launch after an update, shows the notes of the version now
     * installed. A fresh install shows nothing: there is nothing "new" yet.
     */
    private fun showWhatsNewAfterUpdate() {
        viewModelScope.launch {
            val current = BuildConfig.VERSION_NAME
            val lastSeen = repository.lastSeenVersion.first()
            val updated = if (lastSeen == null) {
                // 0.2 and earlier did not record the version: rely on Android's dates.
                wasUpdatedInPlace()
            } else {
                AppVersion.isNewer(current, lastSeen)
            }
            if (lastSeen != current) repository.saveLastSeenVersion(current)
            if (!updated) return@launch

            val markdown = bundledNotes() ?: return@launch
            // Notes left over from an older version would announce the wrong things.
            val notesVersion = ReleaseNotes.titleVersion(markdown) ?: return@launch
            if (!AppVersion.isSame(notesVersion, current)) return@launch
            _state.update { it.copy(whatsNew = WhatsNew(notesVersion, ReleaseNotes.forApp(markdown))) }
        }
    }

    /** Opens the notes of the installed version, from the settings. */
    fun showCurrentVersionNotes() {
        viewModelScope.launch {
            val markdown = bundledNotes() ?: return@launch
            val version = ReleaseNotes.titleVersion(markdown) ?: BuildConfig.VERSION_NAME
            _state.update { it.copy(whatsNew = WhatsNew(version, ReleaseNotes.forApp(markdown))) }
        }
    }

    fun dismissWhatsNew() {
        _state.update { it.copy(whatsNew = null) }
    }

    private suspend fun bundledNotes(): String? = withContext(Dispatchers.IO) {
        runCatching {
            getApplication<Application>().assets.open(RELEASE_NOTES_ASSET).bufferedReader().use { it.readText() }
        }.getOrNull()
    }

    private fun wasUpdatedInPlace(): Boolean = runCatching {
        val app = getApplication<Application>()
        @Suppress("DEPRECATION")
        val info = app.packageManager.getPackageInfo(app.packageName, 0)
        info.lastUpdateTime > info.firstInstallTime
    }.getOrDefault(false)

    // --- roster -------------------------------------------------------------

    private fun newPlayer(index: Int): Player = Player(
        id = UUID.randomUUID().toString(),
        name = Avatars.DEFAULT_NAMES[index % Avatars.DEFAULT_NAMES.size],
        avatar = index % Avatars.count,
    )

    fun addPlayer() {
        val current = _state.value.players
        if (current.size >= GameEngine.MAX_PLAYERS) return
        persistPlayers(current + newPlayer(current.size))
    }

    fun removePlayer(id: String) {
        val current = _state.value.players
        if (current.size <= 1) return
        val remaining = current.filterNot { it.id == id }
        persistPlayers(remaining)
        clampImposterCount(remaining.size)
    }

    fun renamePlayer(id: String, name: String) {
        persistPlayers(_state.value.players.map { if (it.id == id) it.copy(name = name) else it })
    }

    fun cycleAvatar(id: String) {
        persistPlayers(
            _state.value.players.map {
                if (it.id == id) it.copy(avatar = (it.avatar + 1) % Avatars.count) else it
            },
        )
    }

    fun resetScores() {
        persistPlayers(_state.value.players.map { it.copy(score = 0) })
    }

    private fun persistPlayers(players: List<Player>) {
        _state.value = _state.value.copy(players = players)
        viewModelScope.launch { repository.savePlayers(players) }
    }

    /** Keeps the impostor count legal when the table shrinks. */
    private fun clampImposterCount(playerCount: Int) {
        val max = GameEngine.maxImposters(playerCount)
        val settings = _state.value.settings
        if (settings.imposterCount > max) {
            updateSettings(settings.copy(imposterCount = max))
        }
    }

    // --- settings -----------------------------------------------------------

    fun updateSettings(settings: GameSettings) {
        _state.value = _state.value.copy(settings = settings)
        viewModelScope.launch { repository.saveSettings(settings) }
    }

    fun setImposterCount(count: Int) {
        val max = GameEngine.maxImposters(_state.value.players.size)
        updateSettings(_state.value.settings.copy(imposterCount = count.coerceIn(1, max)))
    }

    fun togglePack(packId: String) {
        val settings = _state.value.settings
        val selected = settings.selectedPackIds.toMutableSet()
        if (!selected.remove(packId)) selected.add(packId)
        // Never let the player start with nothing selected.
        if (selected.isEmpty()) return
        updateSettings(settings.copy(selectedPackIds = selected))
    }

    // --- round flow ---------------------------------------------------------

    fun startGame() {
        val current = _state.value
        when (val result = GameEngine.deal(current.players, current.settings, current.allPacks)) {
            is DealResult.Failure -> _state.value = current.copy(error = result.error)
            is DealResult.Success -> {
                _state.value = current.copy(
                    phase = GamePhase.REVEAL,
                    round = result.round,
                    roundNumber = current.roundNumber + 1,
                    revealIndex = 0,
                    alivePlayerIds = current.players.map { it.id },
                    lastElimination = null,
                    outcome = null,
                    guessWasCorrect = null,
                    error = null,
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun nextReveal() {
        val current = _state.value
        if (current.isLastReveal) {
            _state.value = current.copy(phase = GamePhase.DISCUSSION)
        } else {
            _state.value = current.copy(revealIndex = current.revealIndex + 1)
        }
    }

    fun goToVote() {
        _state.value = _state.value.copy(phase = GamePhase.VOTE)
    }

    fun backToDiscussion() {
        _state.value = _state.value.copy(phase = GamePhase.DISCUSSION)
    }

    // --- voting -------------------------------------------------------------

    fun eliminate(playerId: String) {
        val current = _state.value
        val round = current.round ?: return
        val result = GameEngine.eliminate(round, current.alivePlayerIds, playerId)
        _state.value = current.copy(
            phase = GamePhase.ELIMINATION,
            lastElimination = result,
            alivePlayerIds = result.remainingPlayerIds,
        )
    }

    /** Called from the elimination screen once the reveal animation has been read. */
    fun continueAfterElimination() {
        val current = _state.value
        val result = current.lastElimination ?: return
        val outcome = result.outcome

        when {
            // A caught impostor gets a shot at stealing the round.
            outcome == RoundOutcome.CIVILS_WIN &&
                result.wasImposter &&
                current.settings.allowImposterGuess -> {
                _state.value = current.copy(phase = GamePhase.IMPOSTER_GUESS)
            }

            outcome != null -> finishRound(outcome)

            else -> _state.value = current.copy(phase = GamePhase.DISCUSSION)
        }
    }

    fun submitGuess(guess: String) {
        val current = _state.value
        val round = current.round ?: return
        val correct = GameEngine.isGuessCorrect(round, guess)
        _state.value = current.copy(guessWasCorrect = correct)
        finishRound(if (correct) RoundOutcome.IMPOSTERS_STEAL else RoundOutcome.CIVILS_WIN)
    }

    fun skipGuess() {
        _state.value = _state.value.copy(guessWasCorrect = false)
        finishRound(RoundOutcome.CIVILS_WIN)
    }

    private fun finishRound(outcome: RoundOutcome) {
        val current = _state.value
        val round = current.round ?: return
        val scored = Scoring.applyOutcome(current.players, round, outcome)
        _state.value = current.copy(
            phase = GamePhase.ROUND_END,
            outcome = outcome,
            players = scored,
        )
    }

    fun quitToMenu() {
        _state.value = _state.value.copy(
            phase = GamePhase.IDLE,
            round = null,
            revealIndex = 0,
            alivePlayerIds = emptyList(),
            lastElimination = null,
            outcome = null,
            guessWasCorrect = null,
        )
    }

    private companion object {
        /** Bundled copy of the notes published with the release (app/src/main/assets). */
        const val RELEASE_NOTES_ASSET = "RELEASE_NOTES.md"
        const val DOWNLOAD_FAILED = "Le téléchargement a échoué. Réessaie."
    }
}
