package com.toftmalone.imposteur.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.toftmalone.imposteur.R

/** Ratio de la planche du logo (160 x 180). */
private const val LOGO_ASPECT_RATIO = 160f / 180f

/**
 * Le personnage du logo : un buste tenant un masque de théâtre, dessiné sans
 * fond pour qu'il se détache directement sur le dégradé de l'accueil et
 * déborde sur le mot « IMPOSTEUR ».
 */
@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_imposteur),
        contentDescription = null,
        modifier = modifier.aspectRatio(LOGO_ASPECT_RATIO),
        contentScale = ContentScale.Fit,
    )
}
