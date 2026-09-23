package com.toftmalone.imposteur.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.toftmalone.imposteur.BuildConfig
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.toftmalone.imposteur.game.GamePhase
import com.toftmalone.imposteur.game.GameViewModel
import com.toftmalone.imposteur.ui.screens.DiscussionScreen
import com.toftmalone.imposteur.ui.screens.EliminationScreen
import com.toftmalone.imposteur.ui.screens.HomeScreen
import com.toftmalone.imposteur.ui.screens.ImposterGuessScreen
import com.toftmalone.imposteur.ui.screens.PacksScreen
import com.toftmalone.imposteur.ui.screens.RevealScreen
import com.toftmalone.imposteur.ui.screens.RoundEndScreen
import com.toftmalone.imposteur.ui.screens.RulesScreen
import com.toftmalone.imposteur.ui.screens.SettingsScreen
import com.toftmalone.imposteur.ui.screens.SetupScreen
import com.toftmalone.imposteur.ui.screens.VoteScreen
import com.toftmalone.imposteur.ui.theme.Ink

private object Routes {
    const val HOME = "home"
    const val SETUP = "setup"
    const val PACKS = "packs"
    const val RULES = "rules"
    const val SETTINGS = "settings"
    const val GAME = "game"
}

@Composable
fun ImposteurApp(viewModel: GameViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Hands the release page to whatever browser the phone uses.
    fun openReleases() {
        val url = state.availableUpdate?.releaseUrl ?: viewModel.releasesPageUrl()
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = Modifier.fillMaxSize().background(Ink),
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                playerCount = state.players.size,
                packCount = state.selectedPackCount,
                updateVersion = state.availableUpdate?.versionName,
                onUpdateClick = ::openReleases,
                onDismissUpdate = viewModel::dismissUpdateBanner,
                onPlay = { navController.navigate(Routes.SETUP) },
                onPacks = { navController.navigate(Routes.PACKS) },
                onRules = { navController.navigate(Routes.RULES) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
            )
        }

        composable(Routes.SETUP) {
            SetupScreen(
                players = state.players,
                settings = state.settings,
                packCount = state.selectedPackCount,
                wordCount = state.selectedWordCount,
                error = state.error,
                onAddPlayer = viewModel::addPlayer,
                onRemovePlayer = viewModel::removePlayer,
                onRenamePlayer = viewModel::renamePlayer,
                onCycleAvatar = viewModel::cycleAvatar,
                onImposterCountChange = viewModel::setImposterCount,
                onImposterModeChange = { viewModel.updateSettings(state.settings.copy(imposterMode = it)) },
                onOpenPacks = { navController.navigate(Routes.PACKS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onStart = {
                    viewModel.startGame()
                    // A failed deal leaves the phase idle and surfaces an error here
                    // instead of pushing an empty game screen.
                    if (viewModel.state.value.phase != GamePhase.IDLE) {
                        navController.navigate(Routes.GAME)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.PACKS) {
            PacksScreen(
                packs = state.allPacks,
                selectedIds = state.settings.selectedPackIds,
                onToggle = viewModel::togglePack,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.RULES) {
            RulesScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                settings = state.settings,
                impostersPossible = state.settings.imposterCount > 1,
                appVersion = BuildConfig.VERSION_NAME,
                updateVersion = state.availableUpdate?.versionName,
                updateOutcome = state.updateCheckOutcome,
                checkingForUpdate = state.checkingForUpdate,
                onCheckForUpdate = { viewModel.checkForUpdate(userInitiated = true) },
                onOpenReleases = ::openReleases,
                onSettingsChange = viewModel::updateSettings,
                onResetScores = viewModel::resetScores,
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.GAME) {
            GameHost(
                viewModel = viewModel,
                navController = navController,
            )
        }
    }
}

/** Swaps in the right in-game screen for the current phase. */
@Composable
private fun GameHost(
    viewModel: GameViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current
    var showQuitDialog by remember { mutableStateOf(false) }
    var showRules by remember { mutableStateOf(false) }

    fun leaveGame() {
        viewModel.quitToMenu()
        navController.popBackStack(Routes.HOME, inclusive = false)
    }

    // A round that never got dealt (validation failed) must not leave a blank screen.
    LaunchedEffect(state.phase) {
        if (state.phase == GamePhase.IDLE) {
            navController.popBackStack(Routes.HOME, inclusive = false)
        }
    }

    BackHandler(enabled = true) {
        // Back closes the rules overlay first; only then does it offer to quit.
        if (showRules) showRules = false else showQuitDialog = true
    }

    if (showRules) {
        RulesScreen(onBack = { showRules = false })
        return
    }

    val round = state.round
    when {
        round == null -> Unit

        state.phase == GamePhase.REVEAL -> {
            val player = state.revealPlayer
            if (player != null) {
                // A fresh screen per player: nothing (flip, exit animations, button
                // colour) may carry the next player's role over from the previous one.
                key(player.id) {
                    RevealScreen(
                        player = player,
                        assignment = round.assignmentFor(player.id),
                        position = state.revealIndex + 1,
                        total = round.speakingOrder.size,
                        isLast = state.isLastReveal,
                        onNext = {
                            if (state.settings.hapticsEnabled) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            viewModel.nextReveal()
                        },
                        onQuit = { showQuitDialog = true },
                        onHelp = { showRules = true },
                    )
                }
            }
        }

        state.phase == GamePhase.DISCUSSION -> DiscussionScreen(
            roundNumber = state.roundNumber,
            themeName = round.packName.takeIf { state.settings.showCategoryToEveryone },
            speakingOrder = state.speakingPlayers,
            onVote = viewModel::goToVote,
            onQuit = { showQuitDialog = true },
            onHelp = { showRules = true },
        )

        state.phase == GamePhase.VOTE -> VoteScreen(
            candidates = state.alivePlayers,
            onEliminate = {
                if (state.settings.hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                viewModel.eliminate(it)
            },
            onBack = viewModel::backToDiscussion,
            onHelp = { showRules = true },
        )

        state.phase == GamePhase.ELIMINATION -> {
            val elimination = state.lastElimination
            val player = elimination?.let { state.playerById(it.eliminatedId) }
            if (elimination != null && player != null) {
                EliminationScreen(
                    player = player,
                    wasImposter = elimination.wasImposter,
                    roundContinues = elimination.outcome == null,
                    onContinue = viewModel::continueAfterElimination,
                )
            }
        }

        state.phase == GamePhase.IMPOSTER_GUESS -> ImposterGuessScreen(
            imposterNames = round.imposterIds.mapNotNull { state.playerById(it)?.name },
            onSubmit = viewModel::submitGuess,
            onSkip = viewModel::skipGuess,
        )

        state.phase == GamePhase.ROUND_END -> {
            val outcome = state.outcome
            if (outcome != null) {
                RoundEndScreen(
                    outcome = outcome,
                    civilWord = round.civilWord,
                    imposterWord = round.imposterWord,
                    themeName = round.packName,
                    imposters = round.imposterIds.mapNotNull(state::playerById),
                    scoreboard = state.players,
                    guessWasCorrect = state.guessWasCorrect,
                    onNextRound = viewModel::startGame,
                    onMenu = ::leaveGame,
                )
            }
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Quitter la partie ?", style = MaterialTheme.typography.titleLarge) },
            text = { Text("La manche en cours sera perdue. Les scores déjà marqués sont conservés.") },
            confirmButton = {
                TextButton(onClick = {
                    showQuitDialog = false
                    leaveGame()
                }) { Text("Quitter") }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) { Text("Continuer") }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}
