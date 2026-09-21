package com.toftmalone.imposteur.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toftmalone.imposteur.data.Avatars
import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.ImposteurRepository
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.data.Round
import com.toftmalone.imposteur.data.RoundOutcome
import com.toftmalone.imposteur.data.WordPack
import com.toftmalone.imposteur.data.WordPacks
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    val customPacks: List<WordPack> = emptyList(),
    val phase: GamePhase = GamePhase.IDLE,
    val round: Round? = null,
    val roundNumber: Int = 0,
    val revealIndex: Int = 0,
    val alivePlayerIds: List<String> = emptyList(),
    val lastElimination: EliminationResult? = null,
    val outcome: RoundOutcome? = null,
    val guessWasCorrect: Boolean? = null,
    val error: GameError? = null,
) {
    val allPacks: List<WordPack> get() = WordPacks.BUILT_IN + customPacks

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

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.players,
                repository.settings,
                repository.customPacks,
            ) { players, settings, packs -> Triple(players, settings, packs) }
                .collect { (storedPlayers, settings, packs) ->
                    val current = _state.value
                    // Scores are not persisted, so re-apply the ones this
                    // session has earned rather than resetting mid-game.
                    val sessionScores = current.players.associate { it.id to it.score }
                    val merged = storedPlayers.map { it.copy(score = sessionScores[it.id] ?: 0) }
                    _state.value = current.copy(
                        players = merged.ifEmpty { current.players },
                        settings = settings,
                        customPacks = packs,
                    )
                }
        }
        viewModelScope.launch {
            // Seed a starter roster the first time the app is opened.
            if (repository.players.first().isEmpty()) {
                val starter = (0 until 4).map { index -> newPlayer(index) }
                repository.savePlayers(starter)
            }
        }
    }

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

    // --- custom packs -------------------------------------------------------

    fun saveCustomPack(pack: WordPack) {
        val existing = _state.value.customPacks
        val updated = if (existing.any { it.id == pack.id }) {
            existing.map { if (it.id == pack.id) pack else it }
        } else {
            existing + pack
        }
        _state.value = _state.value.copy(customPacks = updated)
        viewModelScope.launch { repository.saveCustomPacks(updated) }

        // A freshly created pack is selected so it can be played straight away.
        if (existing.none { it.id == pack.id } && pack.isPlayable) {
            updateSettings(
                _state.value.settings.copy(
                    selectedPackIds = _state.value.settings.selectedPackIds + pack.id,
                ),
            )
        }
    }

    fun deleteCustomPack(packId: String) {
        val updated = _state.value.customPacks.filterNot { it.id == packId }
        _state.value = _state.value.copy(customPacks = updated)
        viewModelScope.launch { repository.saveCustomPacks(updated) }

        val selected = _state.value.settings.selectedPackIds - packId
        updateSettings(
            _state.value.settings.copy(
                selectedPackIds = selected.ifEmpty { GameSettings.DEFAULT_SELECTED_PACKS },
            ),
        )
    }

    fun newCustomPackId(): String = "custom-${UUID.randomUUID()}"

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
}
