package com.example.aurorah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DeepBurgundy,
    onPrimary = Color.White,
    primaryContainer = BurgundyContainerLight,
    onPrimaryContainer = OnBurgundyContainerLight,
    secondary = BurgundyLight,
    onSecondary = Color.White,
    secondaryContainer = BurgundyContainerLight,
    onSecondaryContainer = OnBurgundyContainerLight,
    tertiary = BurgundyDark,
    onTertiary = Color.White,
    background = CleanWhite,
    onBackground = Color(0xFF1C1B1F),
    surface = CardGrayLight,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F1F3),
    onSurfaceVariant = Color(0xFF49454F),
    outline = CardBorderLight,
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DeepBurgundyDark,
    onPrimary = Color(0xFF3B0513),
    primaryContainer = BurgundyContainerDark,
    onPrimaryContainer = OnBurgundyContainerDark,
    secondary = BurgundyLightDark,
    onSecondary = Color(0xFF3B0513),
    secondaryContainer = BurgundyContainerDark,
    onSecondaryContainer = OnBurgundyContainerDark,
    tertiary = BurgundyLightDark,
    onTertiary = Color(0xFF3B0513),
    background = DarkBackground,
    onBackground = Color(0xFFE6E1E5),
    surface = DarkSurface,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = CardGrayDark,
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = CardBorderDark,
    error = ErrorRedDark,
    onError = Color(0xFF60000B)
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@Composable
fun AurorahTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extendedColors = if (darkTheme) {
        ExtendedColors(
            mintGreenContainer = MintGreenDarkContainer,
            mintGreenText = MintGreenDarkText,
            amberContainer = AmberDarkContainer,
            amberText = AmberDarkText,
            cardBackground = CardGrayDark,
            cardBorder = CardBorderDark
        )
    } else {
        ExtendedColors(
            mintGreenContainer = MintGreenContainer,
            mintGreenText = MintGreenText,
            amberContainer = AmberContainer,
            amberText = AmberText,
            cardBackground = CardGrayLight,
            cardBorder = CardBorderLight
        )
    }

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
