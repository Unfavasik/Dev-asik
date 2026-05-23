package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NatGreenPrimary,
    secondary = NatSubText,
    tertiary = NatAccentGold,
    background = DarkForestBg,
    surface = DarkForestSurface,
    onPrimary = DarkForestBg,
    onSecondary = DarkForestText,
    onTertiary = DarkForestBg,
    onBackground = DarkForestText,
    onSurface = DarkForestText,
    outline = DarkForestBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NatGreenPrimary,
    secondary = NatSecondaryForest,
    tertiary = NatAccentGold,
    background = NatBgLight,
    surface = NatSurface,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onTertiary = PureWhite,
    onBackground = NatTextDark,
    onSurface = NatTextDark,
    outline = NatBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Enforce our custom premium design colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
