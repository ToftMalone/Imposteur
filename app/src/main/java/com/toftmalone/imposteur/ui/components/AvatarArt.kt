package com.toftmalone.imposteur.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.toftmalone.imposteur.R

/**
 * The hand-drawn player portraits, in the order the avatar picker cycles through.
 * They are vector drawables, so they stay sharp from a 34dp scoreboard row up to
 * a full-screen reveal card.
 */
private val AVATAR_DRAWABLES: List<Int> = listOf(
    R.drawable.avatar_detective,
    R.drawable.avatar_dancer,
    R.drawable.avatar_singer,
    R.drawable.avatar_astronaut,
    R.drawable.avatar_scholar,
    R.drawable.avatar_athlete,
    R.drawable.avatar_rocker,
    R.drawable.avatar_chef,
    R.drawable.avatar_climber,
    R.drawable.avatar_gardener,
    R.drawable.avatar_skater,
    R.drawable.avatar_grandpa,
    R.drawable.avatar_pilot,
    R.drawable.avatar_artist,
    R.drawable.avatar_student,
    R.drawable.avatar_sailor,
)

/**
 * Resolves an avatar index to its drawable. The index wraps, so a stale value
 * from a saved roster can never crash the screen; keep [Avatars.COUNT] equal to
 * this list's size so the picker offers every portrait.
 */
@DrawableRes
fun avatarDrawable(index: Int): Int {
    val size = AVATAR_DRAWABLES.size
    return AVATAR_DRAWABLES[((index % size) + size) % size]
}

/** The portrait on its own, with no background — for use over a coloured card. */
@Composable
fun AvatarArtwork(avatar: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(avatarDrawable(avatar)),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/** Fills its parent with the portrait; the caller supplies the background. */
@Composable
internal fun AvatarArtworkFill(avatar: Int) {
    AvatarArtwork(avatar = avatar, modifier = Modifier.fillMaxSize())
}
