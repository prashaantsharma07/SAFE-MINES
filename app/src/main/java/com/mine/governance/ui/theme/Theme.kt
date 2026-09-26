package com.mine.governance.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val IndustrialDarkColorScheme = darkColorScheme(
    primary = WarmActiveBeige,
    onPrimary = DeepWarmStone,
    primaryContainer = BrandEarthBrown,
    onPrimaryContainer = WarmActiveBeige,
    secondary = BrandEarthBrownHover,
    onSecondary = TextPrimary,
    secondaryContainer = SubPanelDark,
    onSecondaryContainer = WarmActiveBeigeSubtle,
    tertiary = WarningOrange,
    background = DeepWarmStone,
    onBackground = TextPrimary,
    surface = DarkCanvas,
    onSurface = TextPrimary,
    surfaceVariant = SubPanelDark,
    onSurfaceVariant = TextSecondary,
    outline = DividerBorderDark,
    error = CriticalRed,
    onError = TextPrimary
)

@Composable
fun MineGovernanceTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = IndustrialDarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DeepWarmStone.toArgb()
                window.navigationBarColor = DeepWarmStone.toArgb()
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
