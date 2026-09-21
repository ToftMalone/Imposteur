package com.toftmalone.imposteur.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary

/**
 * Between the reveal and the vote: an optional countdown plus the speaking order,
 * so the table knows who describes the word next.
 */
@Composable
fun DiscussionScreen(
    roundNumber: Int,
    themeName: String?,
    speakingOrder: List<Player>,
    secondsLeft: Int,
    totalSeconds: Int,
    timerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
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
            CircleIconButton(Icons.Rounded.Close, "Quitter la partie", onQuit)
            Text(
                text = "Manche $roundNumber",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            CircleIconButton(Icons.Rounded.HelpOutline, "Règles du jeu", onHelp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Discussion",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Chacun décrit son mot en un seul indice.\nNe le dites jamais à voix haute !",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            if (themeName != null) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkSurface)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Thème : ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(themeName, style = MaterialTheme.typography.titleMedium, color = BrandOrange)
                }
            }

            Spacer(Modifier.height(22.dp))

            if (totalSeconds > 0) {
                CountdownDial(
                    secondsLeft = secondsLeft,
                    totalSeconds = totalSeconds,
                    modifier = Modifier
                        .fillMaxWidth(0.62f)
                        .aspectRatio(1f),
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    CircleIconButton(
                        icon = if (timerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (timerRunning) "Mettre en pause" else "Démarrer le chrono",
                        onClick = onToggleTimer,
                        background = InkSurface,
                    )
                    CircleIconButton(
                        icon = Icons.Rounded.Refresh,
                        contentDescription = "Réinitialiser le chrono",
                        onClick = onResetTimer,
                        background = InkSurface,
                    )
                }
                Spacer(Modifier.height(26.dp))
            }

            if (speakingOrder.isNotEmpty()) {
                Text(
                    text = "Ordre de parole",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "${speakingOrder.first().name} commence",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(14.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    itemsIndexed(speakingOrder, key = { _, player -> player.id }) { index, player ->
                        SpeakerChip(player, index + 1)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
        }

        PrimaryButton(
            text = "Passer au vote",
            onClick = onVote,
            containerColor = ImposteurRed,
            contentColor = Color.White,
        )
    }
}

@Composable
private fun SpeakerChip(player: Player, position: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp),
    ) {
        AvatarBadge(avatar = player.avatar, size = 56)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "$position. ${player.name}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

/** Ring that empties as the discussion time runs out. */
@Composable
private fun CountdownDial(
    secondsLeft: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (totalSeconds <= 0) 0f else secondsLeft.toFloat() / totalSeconds
    val urgent = secondsLeft in 1..10
    val ringColor = if (urgent) ImposteurRed else BrandOrange

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = size.minDimension * 0.08f
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)

            drawArc(
                color = InkSurface,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatDuration(secondsLeft),
                style = MaterialTheme.typography.displayLarge,
                color = if (urgent) ImposteurRed else TextPrimary,
            )
            if (secondsLeft == 0) {
                Text(
                    text = "Temps écoulé !",
                    style = MaterialTheme.typography.titleMedium,
                    color = ImposteurRed,
                )
            }
        }
    }
}

/** Seconds as mm:ss. */
fun formatDuration(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val minutes = safe / 60
    val seconds = safe % 60
    return "%d:%02d".format(minutes, seconds)
}
