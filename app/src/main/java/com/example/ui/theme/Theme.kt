package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    tertiary = WarmAmber,
    onTertiary = Color.White,
    background = SoftBackground,
    onBackground = TextMain,
    surface = CardSurface,
    onSurface = TextMain,
    surfaceVariant = Color(0xFFEFF5F1),
    onSurfaceVariant = TextMuted,
    outline = BorderSubtle,
    error = StatusNotMet,
    onError = Color.White,
    errorContainer = StatusNotMetContainer,
    onErrorContainer = StatusNotMet
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldSecondary,
    onPrimary = Color.White,
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = Color.White,
    secondary = EmeraldPrimary,
    onSecondary = Color.White,
    background = Color(0xFF0F1E18),
    onBackground = Color(0xFFE2EBE6),
    surface = Color(0xFF162B22),
    onSurface = Color(0xFFE2EBE6),
    surfaceVariant = Color(0xFF1E382D),
    onSurfaceVariant = Color(0xFFA5B8AF),
    outline = Color(0xFF28483B),
    error = Color(0xFFE57373)
)

@Composable
fun YojanaMitraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
