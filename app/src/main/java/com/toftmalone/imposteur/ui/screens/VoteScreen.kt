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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.ui.components.AvatarBadge
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.components.PrimaryButton
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/**
 * The table has argued; now it points a finger. One player is picked and voted
 * out — the group settles ties out loud, the phone only records the verdict.
 */
@Composable
fun VoteScreen(
    candidates: List<Player>,
    onEliminate: (String) -> Unit,
    onBack: () -> Unit,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedId by remember { mutableStateOf<String?>(null) }
    val selected = candidates.firstOrNull { it.id == selectedId }

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
            CircleIconButton(Icons.Rounded.ArrowBack, "Revenir à la discussion", onBack, background = InkSurface)
            CircleIconButton(Icons.Rounded.HelpOutline, "Règles du jeu", onHelp, background = InkSurface)
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text = "Qui est l'imposteur ?",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Mettez-vous d'accord, puis désignez un joueur.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(18.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(candidates, key = { it.id }) { player ->
                VoteTile(
                    player = player,
                    selected = player.id == selectedId,
                    onClick = { selectedId = if (selectedId == player.id) null else player.id },
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        PrimaryButton(
            text = selected?.let { "Éliminer ${it.name}" } ?: "Choisis un joueur",
            onClick = { selectedId?.let(onEliminate) },
            enabled = selected != null,
            containerColor = ImposteurRed,
            contentColor = Color.White,
        )
    }
}

@Composable
private fun VoteTile(
    player: Player,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) ImposteurRed.copy(alpha = 0.22f) else InkSurface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) ImposteurRed else Color.White.copy(alpha = 0.08f),
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AvatarBadge(
            avatar = player.avatar,
            size = 76,
            shape = RoundedCornerShape(22.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = player.name,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}
