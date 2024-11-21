package com.example.expensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1A3A2D),
    onPrimary = Color.White,
    secondary = Color.Gray,
    onSecondary = Color.White,
    background = Color(0xFF1A3A2D),
    onBackground = Color.White,
    surface = Color(0xFF1A3A2D),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A3A2D),
    onPrimary = Color.White,
    secondary = Color(0xFF1A3A2D),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color(0xFF1A3A2D),
    surface = Color.White,
    onSurface = Color(0xFF1A3A2D)
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // Disable dynamic color to keep the theme consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
