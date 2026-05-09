package com.example.cybersim.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Full-screen black background. */
@Composable
fun AppGradientBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground),
        content = content
    )
}

fun Modifier.appTopBarTint(): Modifier =
    background(AppSurfaceElevated.copy(alpha = 0.94f))

fun Modifier.glassCard(
    corner: Dp = 20.dp,
    elevated: Boolean = false,
    neonColor: Color = NeonCyan
): Modifier = this
    .clip(RoundedCornerShape(corner))
    .background(if (elevated) GlassFillElevated else GlassFill)
    .border(2.dp, neonColor, RoundedCornerShape(corner))

fun Modifier.neonCard(
    corner: Dp = 20.dp,
    neonColor: Color = NeonCyan
): Modifier = this
    .clip(RoundedCornerShape(corner))
    .background(AppCard)
    .border(2.dp, neonColor, RoundedCornerShape(corner))

fun Modifier.neonGlow(
    corner: Dp = 16.dp,
    neonColor: Color = NeonCyan,
    elevation: Dp = 0.dp
): Modifier = this
    .border(2.dp, neonColor, RoundedCornerShape(corner))

fun Modifier.floatingShadow(corner: Dp = 20.dp): Modifier =
    shadow(
        elevation = 16.dp,
        shape = RoundedCornerShape(corner),
        ambientColor = Color.Black.copy(alpha = 0.5f),
        spotColor = NeonCyan.copy(alpha = 0.25f)
    )

fun Modifier.softCardShadow(corner: Dp = 16.dp): Modifier =
    shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(corner),
        ambientColor = Color.Black.copy(alpha = 0.4f),
        spotColor = NeonPurple.copy(alpha = 0.15f)
    )

/** Neon accent brush for high-impact elements. */
fun primaryAccentBrush(): Brush = Brush.horizontalGradient(
    colors = listOf(
        NeonPurple.copy(alpha = 0.9f),
        NeonCyan.copy(alpha = 0.8f),
        NeonGreen.copy(alpha = 0.7f)
    )
)
