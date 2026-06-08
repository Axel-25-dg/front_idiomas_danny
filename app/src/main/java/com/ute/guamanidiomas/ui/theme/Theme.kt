package com.ute.guamanidiomas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalIsDarkTheme = compositionLocalOf { false }

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBlue,

    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = LightBlue,
    onSecondaryContainer = DarkBlue,

    tertiary = Color(0xFF475569),
    onTertiary = Color.White,
    tertiaryContainer = SoftBlue,
    onTertiaryContainer = PrimaryBlue,

    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),

    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9),

    error = ErrorColor,
    onError = Color.White,
    errorContainer = SoftBlue,
    onErrorContainer = ErrorColor
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = LightBlue,

    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E3A5F),
    onSecondaryContainer = LightBlue,

    tertiary = Color(0xFF94A3B8),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF1E293B),
    onTertiaryContainer = AccentBlue,

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),

    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),

    error = ErrorColor,
    onError = Color.White,
    errorContainer = Color(0xFF3B1010),
    onErrorContainer = ErrorColor
)

@Composable
fun GuamanIdiomasTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}