package com.bruitage.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BruitageColors = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceDark,
    primary = Accent
)

@Composable
fun BruitageTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BruitageColors,
        content = content
    )
}
