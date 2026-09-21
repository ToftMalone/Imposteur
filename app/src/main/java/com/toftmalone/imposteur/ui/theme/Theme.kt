package com.toftmalone.imposteur.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ImposteurColors = darkColorScheme(
    primary = BrandOrange,
    onPrimary = TextOnLight,
    secondary = ImposteurRed,
    onSecondary = TextPrimary,
    tertiary = CivilBlue,
    onTertiary = TextPrimary,
    background = Ink,
    onBackground = TextPrimary,
    surface = InkSurface,
    onSurface = TextPrimary,
    surfaceVariant = InkSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    outline = InkOutline,
    error = ImposteurRed,
    onError = TextPrimary,
)

/**
 * The app is dark-only on purpose: it is played in living rooms and bars, and a
 * single palette keeps the reveal cards reading the same on every device.
 */
@Composable
fun ImposteurTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = ImposteurColors,
        typography = ImposteurTypography,
        content = content,
    )
}
