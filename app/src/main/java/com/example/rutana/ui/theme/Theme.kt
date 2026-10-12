package com.example.rutana.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RutanaColors = lightColorScheme(
    primary = RutanaNavy,
    onPrimary = Color.White,
    secondary = RutanaYellow,
    onSecondary = RutanaNavyDark,
    background = RutanaBackground,
    onBackground = RutanaNavyDark,
    surface = Color.White,
    onSurface = RutanaNavyDark,
    onSurfaceVariant = RutanaGray
)

@Composable
fun RutanaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RutanaColors,
        typography = Typography,
        content = content
    )
}
