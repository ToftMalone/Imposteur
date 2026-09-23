package com.toftmalone.imposteur.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.ui.components.AvatarArtwork
import com.toftmalone.imposteur.ui.components.BrandBackground
import com.toftmalone.imposteur.ui.components.PrimaryButton
import com.toftmalone.imposteur.ui.components.SecondaryButton
import com.toftmalone.imposteur.ui.theme.BrandRed

/** The front door: a big logo and four ways in. */
@Composable
fun HomeScreen(
    playerCount: Int,
    packCount: Int,
    updateVersion: String?,
    onUpdateClick: () -> Unit,
    onDismissUpdate: () -> Unit,
    onPlay: () -> Unit,
    onPacks: () -> Unit,
    onRules: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BrandBackground(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.weight(0.6f))

            FloatingLogo()

            Spacer(Modifier.height(18.dp))
            Text(
                text = "IMPOSTEUR",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Un mot. Un intrus.\nDémasquez-le avant qu'il ne vous piège.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.weight(1f))

            AnimatedVisibility(visible = updateVersion != null) {
                Column {
                    UpdateBanner(
                        version = updateVersion.orEmpty(),
                        onClick = onUpdateClick,
                        onDismiss = onDismissUpdate,
                    )
                    Spacer(Modifier.height(14.dp))
                }
            }

            PrimaryButton(
                text = "Jouer",
                onClick = onPlay,
                leadingIcon = Icons.Rounded.PlayArrow,
                contentColor = BrandRed,
            )
            Spacer(Modifier.height(12.dp))
            SecondaryButton(text = "Packs de mots", onClick = onPacks)
            Spacer(Modifier.height(10.dp))
            SecondaryButton(text = "Comment jouer", onClick = onRules)
            Spacer(Modifier.height(10.dp))
            SecondaryButton(text = "Paramètres", onClick = onSettings)

            Spacer(Modifier.height(18.dp))
            Text(
                text = "$playerCount joueurs · $packCount packs sélectionnés",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
            )
            Spacer(Modifier.weight(0.2f))
        }
    }
}

/** The detective badge, gently bobbing so the menu feels alive. */
@Composable
private fun FloatingLogo() {
    val transition = rememberInfiniteTransition(label = "logo")
    val float by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float",
    )

    Box(
        modifier = Modifier
            .size(148.dp)
            .graphicsLayer { translationY = float }
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        // Avatar 0 is the detective, which doubles as the app mark.
        AvatarArtwork(avatar = 0, modifier = Modifier.size(126.dp))
    }
}

/** Discreet nudge when GitHub reports a newer release; opens the update window. */
@Composable
private fun UpdateBanner(
    version: String,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = 0.18f))
            .border(1.dp, Color.White.copy(alpha = 0.45f), shape)
            .clickable(onClick = onClick)
            .padding(start = 18.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.FileDownload,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = "Version $version disponible",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
            Text(
                text = "Appuie pour voir les nouveautés",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Masquer",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
