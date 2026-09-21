package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.toftmalone.imposteur.ui.theme.BrandOrange
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextOnLight
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/**
 * Between the reveal and the vote. There is no clock: the table decides when it
 * has heard enough, which keeps the pace conversational rather than timed.
 */
@Composable
fun DiscussionScreen(
    roundNumber: Int,
    themeName: String?,
    speakingOrder: List<Player>,
    onVote: () -> Unit,
    onQuit: () -> Unit,
    onHelp: () -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircleIconButton(Icons.Rounded.Close, "Quitter la partie", onQuit, background = InkSurface)
            Text(
                text = "Manche $roundNumber",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            CircleIconButton(Icons.Rounded.HelpOutline, "Règles du jeu", onHelp, background = InkSurface)
        }

        Spacer(Modifier.height(14.dp))
        Text(
            text = "Discussion",
            style = MaterialTheme.typography.displayMedium,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Chacun décrit son mot en un seul indice.\nNe le dites jamais à voix haute !",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        if (themeName != null) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InkSurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Thème : ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(themeName, style = MaterialTheme.typography.titleMedium, color = BrandOrange)
            }
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "ORDRE DE PAROLE",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.padding(start = 6.dp),
        )
        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(speakingOrder, key = { _, player -> player.id }) { index, player ->
                SpeakerRow(player = player, position = index + 1, isFirst = index == 0)
            }
        }

        Spacer(Modifier.height(12.dp))
        PrimaryButton(
            text = "Passer au vote",
            onClick = onVote,
            containerColor = ImposteurRed,
            contentColor = Color.White,
        )
    }
}

@Composable
private fun SpeakerRow(player: Player, position: Int, isFirst: Boolean) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isFirst) BrandOrange.copy(alpha = 0.16f) else InkSurface)
            .border(
                width = if (isFirst) 2.dp else 1.dp,
                color = if (isFirst) BrandOrange else Color.White.copy(alpha = 0.06f),
                shape = shape,
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$position",
                style = MaterialTheme.typography.titleMedium,
                color = if (isFirst) BrandOrange else TextSecondary,
            )
        }
        AvatarBadge(avatar = player.avatar, size = 44, shape = CircleShape)
        Text(
            text = player.name,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        if (isFirst) {
            Text(
                text = "COMMENCE",
                style = MaterialTheme.typography.labelMedium,
                color = TextOnLight,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(BrandOrange)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}
