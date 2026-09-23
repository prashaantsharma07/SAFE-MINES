package com.mine.governance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.GlassBorderBrush
import com.mine.governance.ui.theme.ImperialGold

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderBrush: Brush = GlassBorderBrush,
    borderWidth: Dp = 1.dp,
    surfaceTint: Color = CavernDark.copy(alpha = 0.75f),
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(color = ImperialGold.copy(alpha = 0.3f)),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .glassEffect(
                shape = shape,
                borderBrush = borderBrush,
                borderWidth = borderWidth,
                surfaceTint = surfaceTint
            )
            .then(clickableModifier)
            .padding(contentPadding),
        content = content
    )
}
