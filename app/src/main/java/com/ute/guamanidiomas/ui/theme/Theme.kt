package com.ute.guamanidiomas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBlue,

    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = LightBlue,
    onSecondaryContainer = DarkBlue,

    tertiary = TextSecondary,
    onTertiary = Color.White,
    tertiaryContainer = SoftBlue,
    onTertiaryContainer = PrimaryBlue,

    background = BackgroundColor,
    onBackground = TextPrimary,

    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = PageBg,
    onSurfaceVariant = TextSecondary,

    outline = Border,
    outlineVariant = Divider,

    error = ErrorColor,
    onError = Color.White,
    errorContainer = SoftBlue, // Ajustado para no usar LightRed inexistente
    onErrorContainer = ErrorColor
)

@Composable
fun GuamanIdiomasTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
