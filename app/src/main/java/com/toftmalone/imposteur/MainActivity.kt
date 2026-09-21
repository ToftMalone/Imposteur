package com.toftmalone.imposteur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.toftmalone.imposteur.ui.ImposteurApp
import com.toftmalone.imposteur.ui.theme.ImposteurTheme

/** Single activity: the whole game lives inside one Compose tree. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ImposteurTheme {
                ImposteurApp()
            }
        }
    }
}
