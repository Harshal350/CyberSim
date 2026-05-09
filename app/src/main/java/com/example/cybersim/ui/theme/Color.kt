package com.example.cybersim.ui.theme

import androidx.compose.ui.graphics.Color

// —— Deep Neon foundations ——
val AppBackground = Color(0xFF000000) // Pure black
val AppSurface = Color(0xFF000000)
val AppSurfaceElevated = Color(0xFF050505)
val AppCard = Color(0xFF080808)
val AppCardMuted = Color(0xFF0A0A0A)

// —— Neon Accent spectrum ——
val NeonGreen = Color(0xFF00FF41)
val NeonCyan = Color(0xFF00F3FF)
val NeonPurple = Color(0xFFBD00FF)
val NeonPink = Color(0xFFFF00E5)
val NeonBlue = Color(0xFF007BFF)

// —— Typography on dark ——
val TextPrimary = NeonCyan
val TextSecondary = NeonCyan.copy(alpha = 0.8f)
val TextTertiary = NeonCyan.copy(alpha = 0.6f)

// —— Semantic Neon ——
val SemanticDanger = Color(0xFFFF3131)
val SemanticWarning = Color(0xFFFFFF33)
val SemanticSuccess = NeonGreen
val SemanticInfo = NeonCyan

// —— Glass & borders (Glowing accents) ——
val GlassFill = Color.White.copy(alpha = 0.03f)
val GlassFillElevated = Color.White.copy(alpha = 0.07f)
val GlassStroke = Color(0xFF222222) // Solid dark grey for standout border
val GlassStrokeFocus = NeonCyan

val OutlineMuted = Color(0xFF2A2A2A)
val DividerSubtle = Color.White.copy(alpha = 0.05f)

// —— Mesh tones for neon gradients ——
val MeshTintIndigo = Color(0xFF120038).copy(alpha = 0.6f)
val MeshTintPurple = Color(0xFF2A004F).copy(alpha = 0.4f)

/** Legacy identifiers — routed to the new neon palette. */
val AccentIndigo = NeonPurple
val AccentPurple = NeonPurple
val ElectricBlue = NeonBlue
val AccentCyan = NeonCyan
val SoftMagenta = NeonPink

val CyberBlack = AppBackground
val CyberDark = AppSurface
val CyberDarker = AppSurfaceElevated
val GlassBackground = GlassFill
val GlassBorder = GlassStroke
val HackerGreen = NeonGreen
val MutedCyan = NeonCyan.copy(alpha = 0.7f)
val SubtleAccent = OutlineMuted
val DangerRed = SemanticDanger
val WarningOrange = SemanticWarning
