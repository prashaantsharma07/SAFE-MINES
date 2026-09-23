package com.mine.governance.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mine.governance.ui.theme.EmergencyCrimson
import com.mine.governance.ui.theme.SafeMinesBlack
import com.mine.governance.ui.theme.SafeMinesDarkGreen
import com.mine.governance.ui.theme.SafeMinesOliveGreen
import com.mine.governance.ui.theme.SafeMinesYellow

/**
 * Applies a frosted Glassmorphism surface effect to any Composable.
 * Updated for SafeMines branding.
 */
fun Modifier.glassEffect(
    shape: Shape = RoundedCornerShape(16.dp),
    blurRadius: Float = 25f,
    borderWidth: Dp = 1.dp,
    borderBrush: Brush = Brush.linearGradient(
        colors = listOf(
            SafeMinesYellow.copy(alpha = 0.50f),
            SafeMinesOliveGreen.copy(alpha = 0.30f),
            Color.White.copy(alpha = 0.08f)
        )
    ),
    surfaceTint: Color = SafeMinesDarkGreen.copy(alpha = 0.72f),
    elevation: Dp = 8.dp
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.6f),
        spotColor = SafeMinesOliveGreen.copy(alpha = 0.2f)
    )
    .clip(shape)
    // Specular highlight layer
    .drawBehind {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.10f),
                    Color.White.copy(alpha = 0.03f),
                    Color.Transparent
                )
            )
        )
    }
    // Translucent dark glass surface tint
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                surfaceTint,
                SafeMinesBlack.copy(alpha = 0.78f)
            )
        )
    )
    // Dual-tone gradient hairline border
    .border(
        width = borderWidth,
        brush = borderBrush,
        shape = shape
    )

/**
 * Pulsating SOS emergency glow modifier for critical alerts in dark mine environments.
 */
@Composable
fun Modifier.crimsonSosPulse(
    shape: Shape = RoundedCornerShape(50)
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "SosPulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AlphaPulse"
    )

    return this
        .shadow(
            elevation = (16 * alphaAnim).dp,
            shape = shape,
            ambientColor = EmergencyCrimson,
            spotColor = EmergencyCrimson.copy(alpha = alphaAnim)
        )
        .border(
            width = (1.5).dp,
            brush = Brush.radialGradient(
                colors = listOf(
                    EmergencyCrimson.copy(alpha = alphaAnim),
                    SafeMinesYellow.copy(alpha = 0.4f)
                )
            ),
            shape = shape
        )
}
