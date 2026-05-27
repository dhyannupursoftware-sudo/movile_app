package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,
    secondary = AccentBlue,
    tertiary = NeonRedGlow,
    background = DarkBg,
    surface = CardBg,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = GlassBg,
    outline = GlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    secondary = AccentRed,
    tertiary = NeonBlueGlow,
    background = LightBg,
    surface = LightCardBg,
    onPrimary = LightTextPrimary,
    onSecondary = LightTextPrimary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFE5E7EB),
    outline = Color(0xFFD1D5DB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force visual cinematic dark default
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
