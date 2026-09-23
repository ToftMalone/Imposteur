package com.toftmalone.imposteur.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.data.Assignment
import com.toftmalone.imposteur.data.Player
import com.toftmalone.imposteur.data.Role
import com.toftmalone.imposteur.ui.components.AvatarArtwork
import com.toftmalone.imposteur.ui.components.AvatarBadge
import com.toftmalone.imposteur.ui.components.CircleIconButton
import com.toftmalone.imposteur.ui.components.PrimaryButton
import com.toftmalone.imposteur.ui.components.UiArt
import com.toftmalone.imposteur.ui.components.UiIcon
import com.toftmalone.imposteur.ui.theme.CivilBlue
import com.toftmalone.imposteur.ui.theme.CivilCardGradient
import com.toftmalone.imposteur.ui.theme.ImposteurCardGradient
import com.toftmalone.imposteur.ui.theme.ImposteurRed
import com.toftmalone.imposteur.ui.theme.Ink
import com.toftmalone.imposteur.ui.theme.InkSurface
import com.toftmalone.imposteur.ui.theme.TextPrimary
import com.toftmalone.imposteur.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/** How long the card takes to turn over. */
private const val FLIP_MILLIS = 560

/**
 * The pass-the-phone screen.
 *
 * The background stays neutral for the whole screen: the player *handing the
 * phone over* is still looking at it while "Passe le téléphone à X" shows, so
 * anything role-coloured at that moment would tell them who the impostor is.
 * The role only ever appears on the card, after it has turned over.
 */
@Composable
fun RevealScreen(
    player: Player,
    assignment: Assignment,
    position: Int,
    total: Int,
    isLast: Boolean,
    onNext: () -> Unit,
    onQuit: () -> Unit,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Reset for each player so nobody inherits the previous card's state.
    var handedOver by remember(player.id) { mutableStateOf(false) }
    var revealed by remember(player.id) { mutableStateOf(false) }

    val isImposter = assignment.role == Role.IMPOSTEUR
    val accent = if (isImposter) ImposteurRed else CivilBlue

    // 0 = the neutral "pass it on" side, 1 = the role side. Owned by the player and
    // only ever animated forwards: flipping back when moving on would briefly show
    // the next player's card, and therefore their role colour, on the reverse side.
    val flipAnim = remember(player.id) { Animatable(0f) }
    LaunchedEffect(player.id, handedOver) {
        if (handedOver) {
            flipAnim.animateTo(1f, tween(durationMillis = FLIP_MILLIS, easing = FastOutSlowInEasing))
        }
    }
    val flip = flipAnim.value

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
            CircleIconButton(
                icon = Icons.Rounded.Close,
                contentDescription = "Quitter la partie",
                onClick = onQuit,
                background = InkSurface,
            )
            Text(
                text = "$position / $total",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
            )
            CircleIconButton(
                icon = Icons.Rounded.HelpOutline,
                contentDescription = "Règles du jeu",
                onClick = onHelp,
                background = InkSurface,
            )
        }

        Spacer(Modifier.height(16.dp))

        // The turning card. Both faces live in the same box so the flip reads as
        // one physical object rather than a cross-fade.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = flip * 180f
                    cameraDistance = 16f * density
                },
        ) {
            if (!handedOver || flip < 0.5f) {
                HandOverPanel(
                    player = player,
                    onReady = { handedOver = true },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                // Counter-rotated, otherwise the back face renders mirrored.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                ) {
                    SecretCard(
                        player = player,
                        assignment = assignment,
                        revealed = revealed,
                        onRevealed = { revealed = true },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // Grows in under the card once it has turned, so nothing jumps.
        AnimatedVisibility(
            visible = handedOver,
            enter = expandVertically(tween(FLIP_MILLIS / 2)) +
                fadeIn(tween(durationMillis = 260, delayMillis = FLIP_MILLIS / 2)),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(18.dp))
                Text(
                    text = if (isImposter) "Imposteur" else "Civil",
                    style = MaterialTheme.typography.displayMedium,
                    color = accent,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isImposter) {
                        "Les imposteurs ont un mot différent.\nSauras-tu bluffer ?"
                    } else {
                        "Les civils ont le même mot.\nTrouve l'intrus !"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(18.dp))
            }
        }

        AnimatedVisibility(
            visible = revealed,
            enter = fadeIn(tween(260)) + scaleIn(tween(260), initialScale = 0.9f),
            exit = fadeOut(),
        ) {
            PrimaryButton(
                text = if (isLast) "Commencer la discussion" else "Passer au suivant",
                onClick = onNext,
                containerColor = accent,
                contentColor = Color.White,
            )
        }
        if (!revealed) Spacer(Modifier.height(62.dp))
    }
}

/**
 * "Give the phone to X" — deliberately neutral, so the player letting go of the
 * phone learns nothing about the next player's role.
 */
@Composable
private fun HandOverPanel(
    player: Player,
    onReady: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pulse = rememberInfiniteTransition(label = "handover")
    val breathe by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathe",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(InkSurface)
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(28.dp))
            .clickable(onClick = onReady)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Passe le téléphone à",
            style = MaterialTheme.typography.titleLarge,
            color = TextSecondary,
        )
        Spacer(Modifier.height(18.dp))
        AvatarBadge(
            avatar = player.avatar,
            size = 132,
            modifier = Modifier.graphicsLayer {
                scaleX = breathe
                scaleY = breathe
            },
        )
        Spacer(Modifier.height(14.dp))
        Text(
            text = player.name,
            style = MaterialTheme.typography.displayLarge,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(26.dp))
        Text(
            text = "Appuie quand tu es prêt",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
    }
}

/**
 * The role side. The portrait card sits on top of the secret; dragging it up
 * past a third of its height slides it away for good.
 */
@Composable
private fun SecretCard(
    player: Player,
    assignment: Assignment,
    revealed: Boolean,
    onRevealed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember(player.id) { Animatable(0f) }
    var cardHeight by remember(player.id) { mutableFloatStateOf(1f) }

    LaunchedEffect(revealed) {
        if (!revealed && offsetY.value != 0f) offsetY.snapTo(0f)
    }

    Box(modifier = modifier.fillMaxWidth()) {
        SecretContent(assignment = assignment, modifier = Modifier.fillMaxSize())

        if (!revealed) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = offsetY.value }
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        if (assignment.role == Role.IMPOSTEUR) {
                            ImposteurCardGradient
                        } else {
                            CivilCardGradient
                        },
                    )
                    .pointerInput(player.id) {
                        cardHeight = size.height.toFloat()
                        detectVerticalDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (-offsetY.value > cardHeight / 3f) {
                                        offsetY.animateTo(
                                            targetValue = -cardHeight * 1.2f,
                                            animationSpec = tween(220, easing = FastOutSlowInEasing),
                                        )
                                        onRevealed()
                                    } else {
                                        offsetY.animateTo(0f, tween(180))
                                    }
                                }
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    // Only upward drags count; the card never travels down.
                                    offsetY.snapTo((offsetY.value + dragAmount).coerceAtMost(0f))
                                }
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                AvatarArtwork(
                    avatar = player.avatar,
                    modifier = Modifier.fillMaxWidth(0.82f),
                )
                SwipeHint(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 26.dp),
                )
            }
        }
    }
}

/** The bouncing arrow and caption on the cover card. */
@Composable
private fun SwipeHint(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "swipe-hint")
    val bounce by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bounce",
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Rounded.ArrowUpward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(34.dp)
                .graphicsLayer { translationY = bounce },
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Glisse vers le haut",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
        )
    }
}

/** What waits under the card: the word, or the impostor's instructions. */
@Composable
private fun SecretContent(
    assignment: Assignment,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(InkSurface)
            .padding(horizontal = 22.dp, vertical = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (assignment.categoryHint != null) {
            Text(
                text = "Thème",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Text(
                text = assignment.categoryHint,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(22.dp))
        }

        if (assignment.word != null) {
            Text(
                text = "Ton mot",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = assignment.word,
                style = MaterialTheme.typography.displayLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
        } else {
            UiIcon(UiArt.MASK, size = 78)
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Tu n'as pas de mot",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Écoute les autres et fais semblant de savoir.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )
        }

        if (assignment.fellowImposters.isNotEmpty()) {
            Spacer(Modifier.height(22.dp))
            Text(
                text = "Tes complices",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
            )
            Text(
                text = assignment.fellowImposters.joinToString(" · "),
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(20.dp))
        Text(
            text = "Retiens-le bien, puis passe le téléphone.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}
