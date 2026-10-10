package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val QuickItLightColorScheme = lightColorScheme(
    primary = RoyalPurple,
    onPrimary = Color.White,
    primaryContainer = SoftLavender,
    onPrimaryContainer = DeepViolet,
    secondary = DeepViolet,
    onSecondary = Color.White,
    secondaryContainer = PastelPurple,
    onSecondaryContainer = DeepViolet,
    tertiary = CyanAccent,
    onTertiary = Color.White,
    tertiaryContainer = IceBlue,
    onTertiaryContainer = DeepViolet,
    background = PearlWhite,
    onBackground = TextPrimary,
    surface = GlassSurface,
    onSurface = TextPrimary,
    surfaceVariant = SoftLavender.copy(alpha = 0.5f),
    onSurfaceVariant = TextSecondary,
    outline = GlassBorderPurple,
    outlineVariant = GlassBorder
)

private val QuickItDarkColorScheme = darkColorScheme(
    primary = PastelPurple,
    onPrimary = DeepViolet,
    primaryContainer = DeepViolet,
    onPrimaryContainer = PearlWhite,
    secondary = SpeedCyan,
    onSecondary = Color.Black,
    secondaryContainer = RoyalPurple,
    onSecondaryContainer = PearlWhite,
    tertiary = LightPink,
    onTertiary = DeepViolet,
    tertiaryContainer = RoyalPurple,
    onTertiaryContainer = PearlWhite,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkGlassSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkGlassBorder,
    outlineVariant = DarkGlassBorder.copy(alpha = 0.5f)
)

@Composable
fun QuickItTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) QuickItDarkColorScheme else QuickItLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
