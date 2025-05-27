package com.example.projet.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val Purple80 = Color(0xFFD0BCFF)
val Purple40 = Color(0xFF6650a4)
val Teal200 = Color(0xFF03DAC5)
val LightColors = lightColorScheme(
    primary = Purple40,
    onPrimary = Color.White,
    secondary = Teal200
)

@Composable
fun MonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography(),
        content = content
    )
}
