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
import com.mine.governance.ui.theme.CriticalRed
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DeepWarmStone
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.SubPanelDark
import com.mine.governance.ui.theme.WarmActiveBeige

/**
 * Applies an industrial frosted panel surface effect.
 * Uses layered dark canvas translucency, subtle specular highlights,
 * and warm active beige/stone hairline borders.
 */
fun Modifier.glassEffect(
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    borderBrush: Brush = Brush.linearGradient(
        colors = listOf(
            WarmActiveBeige.copy(alpha = 0.35f),
            DividerBorderDark.copy(alpha = 0.60f),
            Color.White.copy(alpha = 0.04f)
        )
    ),
    surfaceTint: Color = DarkCanvas.copy(alpha = 0.85f),
    elevation: Dp = 6.dp
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.6f),
        spotColor = WarmActiveBeige.copy(alpha = 0.08f)
    )
    .clip(shape)
    // Subtle top specular edge highlight
    .drawBehind {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.06f),
                    Color.White.copy(alpha = 0.01f),
                    Color.Transparent
                )
            )
        )
    }
    // Deep stone / dark canvas surface background
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                surfaceTint,
                DeepWarmStone.copy(alpha = 0.90f)
            )
        )
    )
    // Industrial hairline border
    .border(
        width = borderWidth,
        brush = borderBrush,
        shape = shape
    )

/**
 * Pulsating SOS emergency glow modifier for critical alerts in mine environments.
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
            ambientColor = CriticalRed,
            spotColor = CriticalRed.copy(alpha = alphaAnim)
        )
        .border(
            width = (1.5).dp,
            brush = Brush.radialGradient(
                colors = listOf(
                    CriticalRed.copy(alpha = alphaAnim),
                    WarmActiveBeige.copy(alpha = 0.4f)
                )
            ),
            shape = shape
        )
}
