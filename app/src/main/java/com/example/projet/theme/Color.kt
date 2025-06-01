package com.example.projet.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Main Colors
val OrangeGourmand = Color(0xFFF26C20)  // Main color
val CremeVanille = Color(0xFFFFF3E0)    // Light background
val ChocolatFondu = Color(0xFF4E342E)   // Text
val TomateSechee = Color(0xFFD84315)    // Button hover
val FeuilleBasilic = Color(0xFF81C784)  // Decorative/Success
val ArdoiseCuisine = Color(0xFF37474F)  // Footer/Contrast

val LightColors = lightColorScheme(
    primary = OrangeGourmand,
    onPrimary = Color.White,
    secondary = TomateSechee,
    tertiary = FeuilleBasilic,
    background = CremeVanille,
    surface = CremeVanille,
    error = TomateSechee,
    onBackground = ChocolatFondu,
    onSurface = ChocolatFondu,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onError = Color.White,
    surfaceVariant = CremeVanille,
    onSurfaceVariant = ChocolatFondu
)

@Composable
fun CookSmartTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}