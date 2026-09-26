package com.mine.governance.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Industrial Color Palette for Coal Mine Smart Governance
 * Grounded in warm deep stone, earth brown, active warm beige, and standard industrial hazard codes.
 */

// Backgrounds & Containers
val DeepWarmStone = Color(0xFF1C1917)       // Base App Background (Dark Canvas)
val DarkCanvas = Color(0xFF292524)          // Card / Panel Surface in dark
val SubPanelDark = Color(0xFF35302D)        // Section hover / Sub-panels dark
val SoftWarmCream = Color(0xFFFDFBF7)       // Base background in light views
val CardSurfaceLight = Color(0xFFFFFFFF)    // Card / Panel Surface in light
val SubPanelLight = Color(0xFFF3EFEA)       // Section hover / Sub-panels light

// Primary & Core Accents
val BrandEarthBrown = Color(0xFF4A3525)     // hsl(28, 45%, 28%) - Logos, header accents, brand markers
val BrandEarthBrownHover = Color(0xFF5C422F)
val WarmActiveBeige = Color(0xFFE8DEC8)     // Active tabs, selection pills, luminous highlights
val WarmActiveBeigeSubtle = Color(0xFFDDD5C7)
val GoldenAmberAccent = Color(0xFFE5A93C)    // High-visibility mine safety highlight

// Industrial Hazard & Status Codes
val CriticalRed = Color(0xFFDC2626)         // Critical Hazard, Level 2 escalation, urgent danger
val WarningOrange = Color(0xFFD97706)       // Warning / Moderate Hazard, Level 1 escalation, pending
val OperationalGreen = Color(0xFF16A34A)    // Operational / Safe, compliant status, online indicator

// Typography & Borders
val TextPrimary = Color(0xFFFDFBF7)         // Primary text on dark backgrounds
val TextSecondary = Color(0xFFA8A29E)       // Muted / Secondary text
val TextMuted = Color(0xFF78716C)           // Minor subtitles, timestamps
val DividerBorderDark = Color(0xFF44403C)   // Clean 1px panel and table borders (dark)
val DividerBorderLight = Color(0xFFE7E5E4)  // Clean 1px borders (light)

// Aliases for component compatibility with previous screens
val ObsidianBlack = DeepWarmStone
val CavernDark = Color(0xFF24201E)
val MineShaftNavy = DarkCanvas
val SurfaceDarkGlass = Color(0x66292524)
val SurfaceLightGlass = Color(0x1AFFFFFF)

val ImperialGold = WarmActiveBeige
val RichGold = GoldenAmberAccent
val BrightGold = WarmActiveBeige
val GoldGlow = Color(0x33E8DEC8)

val CyberCyan = Color(0xFF93C5FD)           // Cool industrial telemetry accent
val DeepNavy = Color(0xFF1C1917)
val CyanGlow = Color(0x2293C5FD)

val EmergencyCrimson = CriticalRed
val CrimsonGlow = Color(0x44DC2626)
val HazardOrange = WarningOrange
val MethaneYellow = WarningOrange
val ComplianceGreen = OperationalGreen
val SafeGreenGlow = Color(0x3316A34A)

// Gradients & Brushes
val IndustrialBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        DeepWarmStone,
        Color(0xFF161413)
    )
)

val DarkGlassBackgroundBrush = IndustrialBackgroundBrush

val EarthBrownButtonBrush = Brush.linearGradient(
    colors = listOf(
        BrandEarthBrownHover,
        BrandEarthBrown,
        Color(0xFF3B2A1D)
    )
)

val ActiveBeigeButtonBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFF5EFE6),
        WarmActiveBeige,
        WarmActiveBeigeSubtle
    )
)

val GoldMetallicBrush = ActiveBeigeButtonBrush

val EmergencyFireBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFEF4444),
        CriticalRed,
        Color(0xFF991B1B)
    )
)

val GlassBorderBrush = Brush.linearGradient(
    colors = listOf(
        WarmActiveBeige.copy(alpha = 0.40f),
        DividerBorderDark.copy(alpha = 0.60f),
        Color.White.copy(alpha = 0.05f)
    )
)
