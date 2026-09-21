package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.data.RoundOutcome
import com.toftmalone.imposteur.ui.components.AvatarBadge
import com.toftmalone.imposteur.ui.components.PrimaryButton
import com.toftmalone.imposteur.ui.components.SecondaryButton
import com.toftmalone.imposteur.ui.components.UiArt
import com.toftmalone.imposteur.ui.components.UiIcon
import com.toftmalone.imposteur.ui.theme.CivilBlue
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.SuccessGreen
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary
import com.toftmalone.imposteur.ui.theme.WarningAmber

/** Reveals what the player the table just voted out really was. */
@Composable
fun EliminationScreen(
    player: Player,
    wasImposter: Boolean,
    roundContinues: Boolean,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = if (wasImposter) SuccessGreen else ImposteurRed

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AvatarBadge(avatar = player.avatar, size = 150)
        Spacer(Modifier.height(22.dp))
        Text(
            text = player.name,
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "était",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (wasImposter) "UN IMPOSTEUR" else "UN CIVIL",
            style = MaterialTheme.typography.displayLarge,
            color = accent,
            textAlign = TextAlign.Center,
        )

        if (roundContinues) {
            Spacer(Modifier.height(18.dp))
            Text(
                text = "La partie continue : il reste un imposteur parmi vous.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(34.dp))
        PrimaryButton(text = "Continuer", onClick = onContinue)
    }
}

/** A caught impostor gets one shot at naming the civilians' word. */
@Composable
fun ImposterGuessScreen(
    imposterNames: List<String>,
    onSubmit: (String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var guess by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(40.dp))
        UiIcon(UiArt.TARGET, size = 78)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Dernière chance",
            style = MaterialTheme.typography.displayMedium,
            color = WarningAmber,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = buildString {
                append(imposterNames.joinToString(" et "))
                append(if (imposterNames.size > 1) " sont démasqués." else " est démasqué.")
                append("\nDevine le mot des civils pour voler la victoire !")
            },
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(26.dp))

        OutlinedTextField(
            value = guess,
            onValueChange = { guess = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Le mot des civils…", color = TextSecondary) },
            textStyle = MaterialTheme.typography.titleLarge,
            shape = RoundedCornerShape(18.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (guess.isNotBlank()) onSubmit(guess) }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InkSurface,
                unfocusedContainerColor = InkSurface,
                focusedBorderColor = WarningAmber,
                unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = WarningAmber,
            ),
        )

        Spacer(Modifier.height(20.dp))
        PrimaryButton(
            text = "Valider ma réponse",
            onClick = { onSubmit(guess) },
            enabled = guess.isNotBlank(),
            containerColor = WarningAmber,
        )
        Spacer(Modifier.height(12.dp))
        SecondaryButton(
            text = "Passer",
            onClick = onSkip,
            borderColor = Color.White.copy(alpha = 0.2f),
        )
        Spacer(Modifier.height(20.dp))
    }
}

/** End-of-round summary: who won, what the word was, and the running scores. */
@Composable
fun RoundEndScreen(
    outcome: RoundOutcome,
    civilWord: String,
    imposterWord: String?,
    themeName: String,
    imposters: List<Player>,
    scoreboard: List<Player>,
    guessWasCorrect: Boolean?,
    onNextRound: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val civilsWon = outcome == RoundOutcome.CIVILS_WIN
    val accent = if (civilsWon) CivilBlue else ImposteurRed

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            UiIcon(if (civilsWon) UiArt.TROPHY else UiArt.MASK, size = 76)
            Spacer(Modifier.height(10.dp))
            Text(
                text = when (outcome) {
                    RoundOutcome.CIVILS_WIN -> "Les civils gagnent !"
                    RoundOutcome.IMPOSTERS_WIN -> "Les imposteurs gagnent !"
                    RoundOutcome.IMPOSTERS_STEAL -> "Victoire volée !"
                },
                style = MaterialTheme.typography.displayMedium,
                color = accent,
                textAlign = TextAlign.Center,
            )

            if (guessWasCorrect != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (guessWasCorrect) {
                        "L'imposteur a deviné le mot juste à temps."
                    } else {
                        "L'imposteur s'est trompé de mot."
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(20.dp))
            WordSummary(
                themeName = themeName,
                civilWord = civilWord,
                imposterWord = imposterWord,
            )

            Spacer(Modifier.height(16.dp))
            ImposterSummary(imposters)

            Spacer(Modifier.height(16.dp))
            Scoreboard(scoreboard)
            Spacer(Modifier.height(16.dp))
        }

        PrimaryButton(text = "Nouvelle manche", onClick = onNextRound)
        Spacer(Modifier.height(10.dp))
        SecondaryButton(
            text = "Retour au menu",
            onClick = onMenu,
            borderColor = Color.White.copy(alpha = 0.2f),
        )
    }
}

@Composable
private fun WordSummary(themeName: String, civilWord: String, imposterWord: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(InkSurface)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(themeName, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(10.dp))
        Text("Le mot était", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Text(
            text = civilWord,
            style = MaterialTheme.typography.headlineLarge,
            color = CivilBlue,
            textAlign = TextAlign.Center,
        )
        if (imposterWord != null) {
            Spacer(Modifier.height(12.dp))
            Text("Les imposteurs avaient", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(
                text = imposterWord,
                style = MaterialTheme.typography.headlineLarge,
                color = ImposteurRed,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ImposterSummary(imposters: List<Player>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(InkSurface)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (imposters.size > 1) "Les imposteurs étaient" else "L'imposteur était",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            imposters.forEach { player ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AvatarBadge(avatar = player.avatar, size = 56)
                    Spacer(Modifier.height(4.dp))
                    Text(player.name, style = MaterialTheme.typography.titleMedium, color = ImposteurRed)
                }
            }
        }
    }
}

/** Running totals, best score first. */
@Composable
fun Scoreboard(players: List<Player>, modifier: Modifier = Modifier) {
    val ranked = players.sortedByDescending { it.score }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(InkSurface)
            .padding(vertical = 14.dp, horizontal = 16.dp),
    ) {
        Text("Scores", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(8.dp))
        ranked.forEachIndexed { index, player ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(end = 10.dp),
                )
                AvatarBadge(avatar = player.avatar, size = 34)
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp),
                    maxLines = 1,
                )
                Text(
                    text = "${player.score}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (index == 0 && player.score > 0) SuccessGreen else TextPrimary,
                )
            }
        }
    }
}
