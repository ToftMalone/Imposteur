package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.GameSettings
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.game.GameEngine
import com.toftmalone.imposteur.game.GameError
import com.toftmalone.imposteur.ui.components.AvatarBadge
import androidx.compose.foundation.border
import com.toftmalone.imposteur.data.ImposterMode
import com.toftmalone.imposteur.ui.components.SectionTitle
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.components.PrimaryButton
import com.toftmalone.imposteur.ui.components.StepperRow
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/** Roster, impostor count and the shortcut into packs, all before dealing. */
@Composable
fun SetupScreen(
    players: List<Player>,
    settings: GameSettings,
    packCount: Int,
    wordCount: Int,
    error: GameError?,
    onAddPlayer: () -> Unit,
    onRemovePlayer: (String) -> Unit,
    onRenamePlayer: (String, String) -> Unit,
    onCycleAvatar: (String) -> Unit,
    onImposterCountChange: (Int) -> Unit,
    onImposterModeChange: (ImposterMode) -> Unit,
    onOpenPacks: () -> Unit,
    onOpenSettings: () -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val maxImposters = GameEngine.maxImposters(players.size)

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.Rounded.ArrowBack, "Retour", onBack, background = InkSurface)
            Text("Joueurs", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            CircleIconButton(Icons.Rounded.Tune, "Réglages de partie", onOpenSettings, background = InkSurface)
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(players, key = { it.id }) { player ->
                PlayerRow(
                    player = player,
                    canRemove = players.size > 1,
                    onRename = { onRenamePlayer(player.id, it) },
                    onCycleAvatar = { onCycleAvatar(player.id) },
                    onRemove = { onRemovePlayer(player.id) },
                )
            }

            item {
                AddPlayerRow(
                    enabled = players.size < GameEngine.MAX_PLAYERS,
                    onClick = onAddPlayer,
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                StepperRow(
                    label = "Imposteurs",
                    supporting = "Maximum $maxImposters pour ${players.size} joueurs",
                    value = settings.imposterCount.toString(),
                    canDecrement = settings.imposterCount > 1,
                    canIncrement = settings.imposterCount < maxImposters,
                    onDecrement = { onImposterCountChange(settings.imposterCount - 1) },
                    onIncrement = { onImposterCountChange(settings.imposterCount + 1) },
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                SectionTitle("Rôle de l'imposteur")
                Spacer(Modifier.height(6.dp))
                ImposterModePicker(
                    selected = settings.imposterMode,
                    onSelect = { onImposterModeChange(it) },
                )
                Spacer(Modifier.height(4.dp))
            }

            item {
                PacksSummaryRow(
                    packCount = packCount,
                    wordCount = wordCount,
                    onClick = onOpenPacks,
                )
            }

            if (error != null) {
                item { ErrorBanner(error) }
            }
        }

        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            text = "Commencer la partie",
            onClick = onStart,
            enabled = players.size >= GameEngine.MIN_PLAYERS && packCount > 0,
            containerColor = BrandOrange,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${GameEngine.MIN_PLAYERS} joueurs minimum · ${GameEngine.MAX_PLAYERS} maximum",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayerRow(
    player: Player,
    canRemove: Boolean,
    onRename: (String) -> Unit,
    onCycleAvatar: () -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AvatarBadge(
            avatar = player.avatar,
            size = 48,
            modifier = Modifier.clickable(onClick = onCycleAvatar),
        )

        BasicTextField(
            value = player.name,
            onValueChange = { onRename(it.take(16)) },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(color = TextPrimary),
            cursorBrush = SolidColor(BrandOrange),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
            decorationBox = { inner ->
                Box {
                    if (player.name.isEmpty()) {
                        Text(
                            text = "Nom du joueur",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary,
                        )
                    }
                    inner()
                }
            },
        )

        if (canRemove) {
            CircleIconButton(
                icon = Icons.Rounded.Close,
                contentDescription = "Retirer ${player.name}",
                onClick = onRemove,
                background = Color.White.copy(alpha = 0.06f),
                tint = TextSecondary,
            )
        }
    }
}

@Composable
private fun AddPlayerRow(enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (enabled) BrandOrange.copy(alpha = 0.16f) else InkSurface)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Rounded.PersonAdd,
            contentDescription = null,
            tint = if (enabled) BrandOrange else TextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = if (enabled) "Ajouter un joueur" else "Table complète",
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) BrandOrange else TextSecondary,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

@Composable
private fun PacksSummaryRow(packCount: Int, wordCount: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(InkSurface)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("Packs de mots", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(
                text = if (packCount == 0) {
                    "Aucun pack sélectionné"
                } else {
                    "$packCount pack${if (packCount > 1) "s" else ""} · $wordCount mots"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (packCount == 0) ImposteurRed else TextSecondary,
            )
        }
        Text("Modifier", style = MaterialTheme.typography.labelMedium, color = BrandOrange)
    }
}

@Composable
private fun ErrorBanner(error: GameError) {
    val message = when (error) {
        GameError.NOT_ENOUGH_PLAYERS -> "Il faut au moins ${GameEngine.MIN_PLAYERS} joueurs pour lancer une partie."
        GameError.TOO_MANY_PLAYERS -> "Maximum ${GameEngine.MAX_PLAYERS} joueurs."
        GameError.TOO_MANY_IMPOSTERS -> "Les imposteurs doivent rester minoritaires."
        GameError.NO_PACK_SELECTED -> "Sélectionne au moins un pack de mots."
        GameError.PACK_TOO_SMALL -> "Ce pack a besoin d'au moins deux mots."
    }
    Text(
        text = message,
        style = MaterialTheme.typography.bodyLarge,
        color = ImposteurRed,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ImposteurRed.copy(alpha = 0.12f))
            .padding(14.dp),
        textAlign = TextAlign.Center,
    )
}

/**
 * Picked right before a game rather than buried in settings: it changes how the
 * round plays, so the table sees it while setting up.
 */
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
