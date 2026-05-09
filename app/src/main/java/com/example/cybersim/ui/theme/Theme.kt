package com.example.cybersim.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberSimColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = AppCard,
    onPrimaryContainer = NeonCyan,
    secondary = NeonGreen,
    onSecondary = Color.Black,
    secondaryContainer = AppCardMuted,
    onSecondaryContainer = NeonGreen,
    tertiary = NeonPink,
    onTertiary = Color.Black,
    tertiaryContainer = AppCard,
    onTertiaryContainer = NeonPink,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = AppSurface,
    onSurface = TextPrimary,
    surfaceVariant = AppSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NeonCyan.copy(alpha = 0.5f),
    outlineVariant = OutlineMuted,
    error = SemanticDanger,
    onError = Color.Black,
    inverseSurface = TextPrimary,
    inverseOnSurface = AppBackground,
)

@Composable
fun CyberSimTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = CyberSimColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = AppBackground.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
