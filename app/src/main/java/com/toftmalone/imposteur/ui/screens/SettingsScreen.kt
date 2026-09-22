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
import androidx.compose.foundation.layout.size
import com.toftmalone.imposteur.data.UpdateCheckOutcome
import com.toftmalone.imposteur.data.WordPacks
import com.toftmalone.imposteur.ui.components.AvatarArtwork
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.SuccessGreen
import com.toftmalone.imposteur.ui.theme.TextSecondary
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
    appVersion: String,
    updateVersion: String?,
    updateOutcome: UpdateCheckOutcome?,
    checkingForUpdate: Boolean,
    onCheckForUpdate: () -> Unit,
    onOpenReleases: () -> Unit,
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

            SectionTitle("Infos de l'app")
            AppInfoCard(
                appVersion = appVersion,
                updateVersion = updateVersion,
                updateOutcome = updateOutcome,
                checkingForUpdate = checkingForUpdate,
                onCheckForUpdate = onCheckForUpdate,
                onOpenReleases = onOpenReleases,
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

/** Version, credits, and the manual update check. */
@Composable
private fun AppInfoCard(
    appVersion: String,
    updateVersion: String?,
    updateOutcome: UpdateCheckOutcome?,
    checkingForUpdate: Boolean,
    onCheckForUpdate: () -> Unit,
    onOpenReleases: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .padding(18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarArtwork(avatar = 0, modifier = Modifier.size(56.dp))
            Column(modifier = Modifier.padding(start = 14.dp)) {
                Text("Imposteur", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Text(
                    text = "Développé par ToftMalone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        InfoLine("Version", appVersion)
        InfoLine("Joueurs", "3 à 20")
        InfoLine("Packs", "${WordPacks.BUILT_IN.size} thèmes")
        InfoLine("Mots", "${WordPacks.BUILT_IN.sumOf { it.pairs.size }} paires")
        InfoLine("Confidentialité", "Aucune donnée ne quitte l'appareil")

        Spacer(Modifier.height(14.dp))

        val status = when {
            checkingForUpdate -> "Vérification en cours…"
            updateVersion != null -> "Version $updateVersion disponible"
            updateOutcome == UpdateCheckOutcome.UP_TO_DATE -> "L'app est à jour"
            updateOutcome == UpdateCheckOutcome.UNAVAILABLE ->
                "Vérification impossible — pas de connexion, ou les releases ne sont pas publiques"
            else -> "Touche pour vérifier les mises à jour"
        }
        val actionable = updateVersion != null

        Text(
            text = if (actionable) "Télécharger la mise à jour" else "Vérifier les mises à jour",
            style = MaterialTheme.typography.labelLarge,
            color = if (actionable) SuccessGreen else BrandOrange,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (actionable) SuccessGreen.copy(alpha = 0.14f) else BrandOrange.copy(alpha = 0.12f),
                )
                .clickable(enabled = !checkingForUpdate) {
                    if (actionable) onOpenReleases() else onCheckForUpdate()
                }
                .padding(vertical = 14.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}
