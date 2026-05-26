package com.pcremote.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Modern Neo-inspired palette
private val NeoBlue = Color(0xFF3D5AFE)
private val NeoCyan = Color(0xFF00E5FF)
private val NeoPurple = Color(0xFF7C4DFF)

private val LightNeoColors = lightColorScheme(
    primary = NeoBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EAF6),
    onPrimaryContainer = NeoBlue,
    secondary = NeoCyan,
    onSecondary = Color.Black,
    tertiary = NeoPurple,
    surface = Color(0xFFF5F7FA),
    background = Color.White,
    surfaceVariant = Color(0xFFE1E4E8),
    onSurfaceVariant = Color(0xFF44474E),
    error = Color(0xFFBA1A1A)
)

private val DarkNeoColors = darkColorScheme(
    primary = Color(0xFFB0C5FF),
    onPrimary = Color(0xFF00297B),
    primaryContainer = Color(0xFF1A237E),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFF00B8D4),
    onSecondary = Color(0xFF00363D),
    tertiary = Color(0xFFD1BCFF),
    surface = Color(0xFF101318),
    background = Color(0xFF0B0E11),
    surfaceVariant = Color(0xFF24292E),
    onSurfaceVariant = Color(0xFFC4C6D0),
    error = Color(0xFFFFB4AB)
)

@Composable
fun PCRemoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkNeoColors else LightNeoColors,
        content = content
    )
}
