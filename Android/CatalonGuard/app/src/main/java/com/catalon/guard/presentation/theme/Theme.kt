package com.catalon.guard.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = RauschRed,
    onPrimary = Color.White,
    primaryContainer = RauschRedDark,
    secondary = BabuTeal,
    onSecondary = Color.White,
    secondaryContainer = BabuTealDark,
    tertiary = AccentAmber,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = Color(0xFFE8E8E8),
    onSurface = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = Color(0xFF444444),
    error = Color(0xFFCF6679)
)

private val LightColorScheme = lightColorScheme(
    primary = RauschRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDADA),
    secondary = BabuTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBE8),
    tertiary = AccentAmber,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onBackground = Color(0xFF1A1A1A),
    onSurface = Color(0xFF1A1A1A),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFFCCCCCC),
    error = Color(0xFFB00020)
)

@Composable
fun CatalonGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(4),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(12),
            large = androidx.compose.foundation.shape.RoundedCornerShape(24)
        ),
        content = content
    )
}
