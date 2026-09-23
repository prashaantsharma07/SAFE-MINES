package com.mine.governance.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// SafeMines Branding Palette
val SafeMinesDarkGreen = Color(0xFF1B3D2B) // Safety / Trust
val SafeMinesOliveGreen = Color(0xFF6B8E23) // Growth / Hope
val SafeMinesYellow = Color(0xFFFFC107)    // Alert / Attention
val SafeMinesBlack = Color(0xFF222222)     // Stability / Strength

// Primary Dark Mine Backgrounds (Obsidian & Deep Void)
val ObsidianBlack = Color(0xFF040608)
val CavernDark = Color(0xFF070B12)
val MineShaftNavy = Color(0xFF0B1320)
val SurfaceDarkGlass = Color(0x33101A2C)
val SurfaceLightGlass = Color(0x1FFFFFFF)

// Golden Accents (Imperial Mine Authority & Status)
val ImperialGold = Color(0xFFFFD700)
val RichGold = Color(0xFFD4AF37)
val BrightGold = Color(0xFFFFE57F)
val MutedGold = Color(0xFF9A7B1C)
val GoldGlow = Color(0x40FFD700)

// Blueish / Cyan Cyber Accents (Telemetry & Sensors)
val CyberCyan = Color(0xFF00E5FF)
val ElectricBlue = Color(0xFF0091FF)
val DeepNavy = Color(0xFF051937)
val CyanGlow = Color(0x3300E5FF)

// Status & Severity Indicators
val EmergencyCrimson = Color(0xFFFF2A44)
val CrimsonGlow = Color(0x55FF2A44)
val HazardOrange = Color(0xFFFF9100)
val MethaneYellow = Color(0xFFFFD600)
val ComplianceGreen = Color(0xFF00E676)
val SafeGreenGlow = Color(0x4400E676)

// Text Colors
val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Glassmorphic Gradient Brushes
val GoldMetallicBrush = Brush.linearGradient(
    colors = listOf(BrightGold, ImperialGold, RichGold)
)

val EmergencyFireBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFFF334B), EmergencyCrimson, Color(0xFF990014))
)

val CyberGlowBrush = Brush.linearGradient(
    colors = listOf(CyberCyan, ElectricBlue)
)

val GlassBorderBrush = Brush.linearGradient(
    colors = listOf(
        ImperialGold.copy(alpha = 0.45f),
        CyberCyan.copy(alpha = 0.25f),
        Color.White.copy(alpha = 0.10f)
    )
)

val DarkGlassBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        CavernDark.copy(alpha = 0.85f),
        MineShaftNavy.copy(alpha = 0.70f)
    )
)
