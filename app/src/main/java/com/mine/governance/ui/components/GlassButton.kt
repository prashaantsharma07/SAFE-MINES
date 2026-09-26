package com.mine.governance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.ui.theme.ActiveBeigeButtonBrush
import com.mine.governance.ui.theme.BrandEarthBrown
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DeepWarmStone
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.EmergencyFireBrush
import com.mine.governance.ui.theme.SubPanelDark
import com.mine.governance.ui.theme.WarmActiveBeige

@Composable
fun GlassGoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    height: Dp = 52.dp
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = WarmActiveBeige.copy(alpha = 0.3f)
            )
            .clip(shape)
            .background(
                if (enabled) ActiveBeigeButtonBrush
                else Brush.linearGradient(listOf(Color(0xFF3F3B37), Color(0xFF292524)))
            )
            .clickable(
                enabled = enabled && !isLoading,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = DeepWarmStone.copy(alpha = 0.3f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = DeepWarmStone,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = DeepWarmStone,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun GlassEmergencyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .crimsonSosPulse(shape = shape)
            .clip(shape)
            .background(EmergencyFireBrush)
            .clickable(
                enabled = !isLoading,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = Color.White.copy(alpha = 0.5f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(26.dp),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}

@Composable
fun GlassSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .glassEffect(
                shape = shape,
                borderBrush = Brush.linearGradient(
                    listOf(WarmActiveBeige.copy(alpha = 0.40f), DividerBorderDark.copy(alpha = 0.6f))
                ),
                surfaceTint = SubPanelDark.copy(alpha = 0.85f)
            )
            .clip(shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = WarmActiveBeige.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = WarmActiveBeige,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
