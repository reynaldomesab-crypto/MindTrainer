package com.mindtrainer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

// Calm, procognitive color palette
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6B7C93),      // Muted blue-gray
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EBF0),
    onPrimaryContainer = Color(0xFF1E2A3A),
    secondary = Color(0xFF8B9BB3),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF0F2F5),
    onSecondaryContainer = Color(0xFF2D3A4A),
    tertiary = Color(0xFFA8B8D0),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF5F6F8),
    onTertiaryContainer = Color(0xFF3A4A5A),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1E2A3A),
    surface = Color.White,
    onSurface = Color(0xFF1E2A3A),
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF4A5A6A),
    outline = Color(0xFFC8CCD4),
    outlineVariant = Color(0xFFE0E4EB),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFFCEDED),
    onErrorContainer = Color(0xFF410E0B),
    surfaceTint = Color(0xFF6B7C93),
    inverseSurface = Color(0xFF2D3A4A),
    inverseOnSurface = Color(0xFFF0F2F5),
    inversePrimary = Color(0xFFA8B8D0),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    surfaceBright = Color.White,
    surfaceDim = Color(0xFFE8EBF0),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA8B8D0),
    onPrimary = Color(0xFF1E2A3A),
    primaryContainer = Color(0xFF4A5A6A),
    onPrimaryContainer = Color(0xFFE8EBF0),
    secondary = Color(0xFF9AA8C0),
    onSecondary = Color(0xFF1E2A3A),
    secondaryContainer = Color(0xFF3A4A5A),
    onSecondaryContainer = Color(0xFFF0F2F5),
    tertiary = Color(0xFFB8C8E0),
    onTertiary = Color(0xFF1E2A3A),
    tertiaryContainer = Color(0xFF4A5A6A),
    onTertiaryContainer = Color(0xFFF5F6F8),
    background = Color(0xFF181C22),
    onBackground = Color(0xFFE8EBF0),
    surface = Color(0xFF1E2A3A),
    onSurface = Color(0xFFE8EBF0),
    surfaceVariant = Color(0xFF2D3A4A),
    onSurfaceVariant = Color(0xFFB8C8E0),
    outline = Color(0xFF4A5A6A),
    outlineVariant = Color(0xFF3A4A5A),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFCEDED),
    surfaceTint = Color(0xFFA8B8D0),
    inverseSurface = Color(0xFFE8EBF0),
    inverseOnSurface = Color(0xFF1E2A3A),
    inversePrimary = Color(0xFF6B7C93),
    shadow = Color(0xFF000000),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF2D3A4A),
    surfaceDim = Color(0xFF181C22),
)

@Composable
fun MindTrainerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

object Typography {
    val displayLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = -0.25.sp
    )
    val displayMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    )
    val displaySmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    )
    val headlineLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )
    val headlineMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 28.sp,
        lineHeight = 36.sp
    )
    val headlineSmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp
    )
    val titleLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    )
    val titleMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )
    val titleSmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    val bodyLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    val bodyMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
    val bodySmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )
    val labelLarge = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
    val labelMedium = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    val labelSmall = androidx.compose.material3.TextStyle(
        fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
}

object Shapes {
    val extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    val small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    val medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    val large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    val extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
    val full = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
}