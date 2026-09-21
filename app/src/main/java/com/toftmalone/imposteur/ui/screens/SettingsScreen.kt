package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.ImposterMode
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.components.SectionTitle
import com.toftmalone.imposteur.ui.components.ToggleRow
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextOnLight
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

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

            SectionTitle("Rôle de l'imposteur")
            ImposterModePicker(
                selected = settings.imposterMode,
                onSelect = { onSettingsChange(settings.copy(imposterMode = it)) },
            )

            SectionTitle("Chronomètre de discussion")
            TimerPicker(
                selected = settings.discussionSeconds,
                onSelect = { onSettingsChange(settings.copy(discussionSeconds = it)) },
            )

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

@Composable
private fun ImposterModePicker(
    selected: ImposterMode,
    onSelect: (ImposterMode) -> Unit,
) {
    val entries = listOf(
        Triple(ImposterMode.DIFFERENT_WORD, "Mot différent", "L'imposteur reçoit un autre mot du même thème."),
        Triple(ImposterMode.CATEGORY_HINT, "Thème seulement", "L'imposteur ne connaît que la catégorie."),
        Triple(ImposterMode.NO_WORD, "Sans indice", "L'imposteur ne sait rien du tout. Mode expert."),
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEach { (mode, title, description) ->
            val isSelected = mode == selected
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) BrandOrange.copy(alpha = 0.16f) else InkSurface)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) BrandOrange else Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .clickable { onSelect(mode) }
                    .padding(horizontal = 18.dp, vertical = 14.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) BrandOrange else TextPrimary,
                )
                Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun TimerPicker(selected: Int, onSelect: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GameSettings.TIMER_CHOICES.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { seconds ->
                    val isSelected = seconds == selected
                    Text(
                        text = if (seconds == 0) "Aucun" else formatDuration(seconds),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) TextOnLight else TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) BrandOrange else Color.White.copy(alpha = 0.05f))
                            .clickable { onSelect(seconds) }
                            .padding(vertical = 12.dp),
                    )
                }
                // Keep the last row aligned with the ones above it.
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}
