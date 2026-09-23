package com.mine.governance.ui.theme

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

private val SafeMinesColorScheme = darkColorScheme(
    primary = SafeMinesOliveGreen,
    onPrimary = SafeMinesBlack,
    primaryContainer = SafeMinesDarkGreen,
    onPrimaryContainer = SafeMinesYellow,
    secondary = SafeMinesYellow,
    onSecondary = SafeMinesBlack,
    secondaryContainer = SafeMinesDarkGreen,
    onSecondaryContainer = SafeMinesOliveGreen,
    tertiary = SafeMinesYellow,
    background = SafeMinesBlack,
    onBackground = TextPrimary,
    surface = SafeMinesDarkGreen,
    onSurface = TextPrimary,
    error = EmergencyCrimson,
    onError = Color.White
)

@Composable
fun MineGovernanceTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = SafeMinesColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SafeMinesBlack.toArgb()
                window.navigationBarColor = SafeMinesBlack.toArgb()
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
