package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.components.SectionTitle
import com.toftmalone.imposteur.ui.components.ToggleRow
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextPrimary

/** Everything that changes how a round plays, plus the score reset. */
@Composable
fun SettingsScreen(
    settings: GameSettings,
    impostersPossible: Boolean,
    onSettingsChange: (GameSettings) -> Unit,
    onResetScores: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ink)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.Rounded.ArrowBack, "Retour", onBack, background = InkSurface)
            Text(
                text = "Paramètres",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.padding(start = 14.dp),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Spacer(Modifier.height(6.dp))

            SectionTitle("Options de partie")
            ToggleRow(
                label = "Afficher le thème",
                supporting = "Le nom du pack apparaît sur la carte des civils.",
                checked = settings.showCategoryToEveryone,
                onCheckedChange = { onSettingsChange(settings.copy(showCategoryToEveryone = it)) },
            )
            ToggleRow(
                label = "Les imposteurs se connaissent",
                supporting = if (impostersPossible) {
                    "Chaque imposteur voit le nom de ses complices."
                } else {
                    "Disponible à partir de 2 imposteurs."
                },
                enabled = impostersPossible,
                checked = settings.impostersKnowEachOther,
                onCheckedChange = { onSettingsChange(settings.copy(impostersKnowEachOther = it)) },
            )
            ToggleRow(
                label = "Ordre de parole aléatoire",
                supporting = "Mélange l'ordre au lieu de suivre la liste des joueurs.",
                checked = settings.randomSpeakingOrder,
                onCheckedChange = { onSettingsChange(settings.copy(randomSpeakingOrder = it)) },
            )
            ToggleRow(
                label = "Dernière chance",
                supporting = "Un imposteur démasqué peut deviner le mot pour voler la manche.",
                checked = settings.allowImposterGuess,
                onCheckedChange = { onSettingsChange(settings.copy(allowImposterGuess = it)) },
            )
            ToggleRow(
                label = "Vibrations",
                supporting = "Retour haptique lors des révélations.",
                checked = settings.hapticsEnabled,
                onCheckedChange = { onSettingsChange(settings.copy(hapticsEnabled = it)) },
            )

            SectionTitle("Scores")
            Text(
                text = "Remettre tous les scores à zéro",
                style = MaterialTheme.typography.labelLarge,
                color = ImposteurRed,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ImposteurRed.copy(alpha = 0.12f))
                    .clickable(onClick = onResetScores)
                    .padding(vertical = 16.dp),
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}
