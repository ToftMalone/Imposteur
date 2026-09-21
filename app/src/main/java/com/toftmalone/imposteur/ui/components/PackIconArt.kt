package com.toftmalone.imposteur.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.toftmalone.imposteur.R
import com.toftmalone.imposteur.data.PackIcons

/** Maps a [PackIcons] key to its drawable. */
private val PACK_ICON_DRAWABLES: Map<String, Int> = mapOf(
    "lion" to R.drawable.pack_lion,
    "globe" to R.drawable.pack_globe,
    "helmet" to R.drawable.pack_helmet,
    "burger" to R.drawable.pack_burger,
    "ball" to R.drawable.pack_ball,
    "clapper" to R.drawable.pack_clapper,
    "tag" to R.drawable.pack_tag,
    "car" to R.drawable.pack_car,
    "tools" to R.drawable.pack_tools,
    "note" to R.drawable.pack_note,
    "gamepad" to R.drawable.pack_gamepad,
    "star" to R.drawable.pack_star,
    "backpack" to R.drawable.pack_backpack,
    "house" to R.drawable.pack_house,
    "laptop" to R.drawable.pack_laptop,
    "party" to R.drawable.pack_party,
    "heart" to R.drawable.pack_heart,
    "shirt" to R.drawable.pack_shirt,
    "temple" to R.drawable.pack_temple,
    "theatre" to R.drawable.pack_theatre,
    "face" to R.drawable.pack_face,
    "eiffel" to R.drawable.pack_eiffel,
    "wizard" to R.drawable.pack_wizard,
    "weather" to R.drawable.pack_weather,
)

/** Falls back to the default artwork so an unknown key never breaks a screen. */
@DrawableRes
fun packIconDrawable(key: String): Int =
    PACK_ICON_DRAWABLES[key] ?: PACK_ICON_DRAWABLES.getValue(PackIcons.DEFAULT)

@Composable
fun PackIcon(icon: String, modifier: Modifier = Modifier, size: Int = 56) {
    Image(
        painter = painterResource(packIconDrawable(icon)),
        contentDescription = null,
        modifier = modifier.size(size.dp),
        contentScale = ContentScale.Fit,
    )
}

/** The illustrated replacements for the decorative emoji the screens used to show. */
enum class UiArt(@DrawableRes val drawable: Int) {
    PHONE(R.drawable.ic_phone),
    PEOPLE(R.drawable.ic_people),
    SPEECH(R.drawable.ic_speech),
    VOTE(R.drawable.ic_vote),
    TROPHY(R.drawable.ic_trophy),
    TARGET(R.drawable.ic_target),
    MASK(R.drawable.ic_mask),
}

@Composable
fun UiIcon(art: UiArt, modifier: Modifier = Modifier, size: Int = 48) {
    Image(
        painter = painterResource(art.drawable),
        contentDescription = null,
        modifier = modifier.size(size.dp),
        contentScale = ContentScale.Fit,
    )
}
