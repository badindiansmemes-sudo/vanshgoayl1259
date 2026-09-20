package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TeenPattiColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color(0xFF1A1200),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = CasinoGreenLight,
    onSecondary = Color.White,
    secondaryContainer = CasinoGreenPrimary,
    onSecondaryContainer = Color(0xFFA3E9C4),
    tertiary = GoldAccent,
    onTertiary = Color.Black,
    background = CasinoGreenDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = StatusRejected,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TeenPattiColorScheme,
        typography = Typography,
        content = content
    )
}
