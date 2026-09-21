package com.toftmalone.imposteur.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Brand ---------------------------------------------------------------------

val BrandOrange = Color(0xFFFF8A34)
val BrandRed = Color(0xFFE3264F)
val BrandDeepRed = Color(0xFFB81742)

/** The warm menu backdrop used across the out-of-game screens. */
val BrandGradient = Brush.verticalGradient(listOf(BrandOrange, BrandRed, BrandDeepRed))

// Roles ---------------------------------------------------------------------

val ImposteurRed = Color(0xFFF2415A)
val CivilBlue = Color(0xFF2E7FF7)

val ImposteurCardGradient = Brush.verticalGradient(listOf(Color(0xFF9B2BFF), Color(0xFF3D1584)))
val CivilCardGradient = Brush.verticalGradient(listOf(Color(0xFFFFC400), Color(0xFF9E1410)))
val HiddenCardGradient = Brush.verticalGradient(listOf(Color(0xFF2B2B38), Color(0xFF15151D)))

// Dark surfaces -------------------------------------------------------------

val Ink = Color(0xFF0B0B10)
val InkSurface = Color(0xFF16161F)
val InkSurfaceHigh = Color(0xFF1F1F2B)
val InkOutline = Color(0xFF2E2E3D)

val SuccessGreen = Color(0xFF22C55E)
val SuccessGreenDim = Color(0xFF0E2E17)
val WarningAmber = Color(0xFFFFB020)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB4B4C4)
val TextOnLight = Color(0xFF16161F)

/** Gradients used behind player avatars, picked by avatar index. */
val AvatarGradients: List<Brush> = listOf(
    Brush.verticalGradient(listOf(Color(0xFFFF9A3D), Color(0xFFE23E3E))),
    Brush.verticalGradient(listOf(Color(0xFF4FC3F7), Color(0xFF1565C0))),
    Brush.verticalGradient(listOf(Color(0xFF9B5CFF), Color(0xFF4A1E9E))),
    Brush.verticalGradient(listOf(Color(0xFF34D399), Color(0xFF047857))),
    Brush.verticalGradient(listOf(Color(0xFFFFD34E), Color(0xFFE08900))),
    Brush.verticalGradient(listOf(Color(0xFFFF7BA9), Color(0xFFC2185B))),
    Brush.verticalGradient(listOf(Color(0xFF7DD3FC), Color(0xFF0E7490))),
    Brush.verticalGradient(listOf(Color(0xFFA3E635), Color(0xFF4D7C0F))),
    Brush.verticalGradient(listOf(Color(0xFFFCA5A5), Color(0xFF991B1B))),
    Brush.verticalGradient(listOf(Color(0xFFC4B5FD), Color(0xFF5B21B6))),
    Brush.verticalGradient(listOf(Color(0xFF5EEAD4), Color(0xFF0F766E))),
    Brush.verticalGradient(listOf(Color(0xFFFDBA74), Color(0xFFC2410C))),
)

fun avatarGradient(index: Int): Brush =
    AvatarGradients[((index % AvatarGradients.size) + AvatarGradients.size) % AvatarGradients.size]
