package com.example.aurorah.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Primary Burgundy Palette
val DeepBurgundy = Color(0xFF8B263E)
val BurgundyLight = Color(0xFFA63A50)
val BurgundyDark = Color(0xFF5F1528)
val BurgundyContainerLight = Color(0xFFFCE8EC)
val OnBurgundyContainerLight = Color(0xFF3B0513)

val DeepBurgundyDark = Color(0xFFEE8098)
val BurgundyLightDark = Color(0xFFF4A3B3)
val BurgundyDarkDark = Color(0xFF5F1528)
val BurgundyContainerDark = Color(0xFF721E33)
val OnBurgundyContainerDark = Color(0xFFFCE8EC)

// Neutral & Background Colors
val CleanWhite = Color(0xFFFFFFFF)
val CardGrayLight = Color(0xFFF8F9FA)
val CardBorderLight = Color(0xFFE5E7EB)

val DarkBackground = Color(0xFF141113)
val DarkSurface = Color(0xFF1F1A1C)
val CardGrayDark = Color(0xFF2A2427)
val CardBorderDark = Color(0xFF3D3438)

// Status & Special Colors
val MintGreenContainer = Color(0xFFE6F4EA)
val MintGreenText = Color(0xFF1E8E3E)
val MintGreenDarkContainer = Color(0xFF0F381E)
val MintGreenDarkText = Color(0xFF81C784)

val AmberContainer = Color(0xFFFFF8E1)
val AmberText = Color(0xFFB45309)
val AmberDarkContainer = Color(0xFF3E2723)
val AmberDarkText = Color(0xFFFFB74D)

val ErrorRed = Color(0xFFB00020)
val ErrorRedDark = Color(0xFFCF6679)

@Immutable
data class ExtendedColors(
    val mintGreenContainer: Color,
    val mintGreenText: Color,
    val amberContainer: Color,
    val amberText: Color,
    val cardBackground: Color,
    val cardBorder: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        mintGreenContainer = MintGreenContainer,
        mintGreenText = MintGreenText,
        amberContainer = AmberContainer,
        amberText = AmberText,
        cardBackground = CardGrayLight,
        cardBorder = CardBorderLight
    )
}
