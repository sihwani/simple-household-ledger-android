package com.sihwani.simpleledger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LedgerLightColorScheme = lightColorScheme(
    primary = Color(0xFF18181B),
    onPrimary = Color.White,
    secondary = Color(0xFF047857),
    onSecondary = Color.White,
    error = Color(0xFFBE123C),
    onError = Color.White,
    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF18181B),
    surface = Color.White,
    onSurface = Color(0xFF18181B),
    surfaceVariant = Color(0xFFF4F4F5),
    onSurfaceVariant = Color(0xFF71717A),
    outline = Color(0xFFD4D4D8)
)

@Composable
fun LedgerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LedgerLightColorScheme,
        content = content
    )
}
