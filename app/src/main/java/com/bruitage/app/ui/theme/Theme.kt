package com.bruitage.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    background = BackgroundDark,
    surface = SurfaceDark,
    primary = Accent
)

private val LightColors = lightColorScheme(
    background = BackgroundLight,
    surface = SurfaceLight,
    primary = AccentLight
)

@Composable
fun BruitageTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
