package com.mine.governance.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.ui.theme.ComplianceGreen
import com.mine.governance.ui.theme.CyberCyan
import com.mine.governance.ui.theme.EmergencyCrimson
import com.mine.governance.ui.theme.HazardOrange
import com.mine.governance.ui.theme.ImperialGold
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary

@Composable
fun ConnectivityCard(
    networkMode: NetworkMode,
    pendingSyncCount: Int,
    lastSyncTime: String,
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
    onToggleTestMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor by animateColorAsState(
        targetValue = when (networkMode) {
            NetworkMode.ONLINE -> ComplianceGreen
            NetworkMode.WEAK_NETWORK -> HazardOrange
            NetworkMode.OFFLINE -> EmergencyCrimson
        },
        label = "StatusColor"
    )

    val statusIcon = when (networkMode) {
        NetworkMode.ONLINE -> Icons.Default.CloudDone
        NetworkMode.WEAK_NETWORK -> Icons.Default.NetworkCheck
        NetworkMode.OFFLINE -> Icons.Default.CloudOff
    }

    val statusLabel = when (networkMode) {
        NetworkMode.ONLINE -> "Online (Central Cloud)"
        NetworkMode.WEAK_NETWORK -> "Weak Network (Mine Gateway)"
        NetworkMode.OFFLINE -> "Offline Mode (Subterranean)"
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderBrush = Brush.linearGradient(
            listOf(statusColor.copy(alpha = 0.5f), CyberCyan.copy(alpha = 0.2f))
        )
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Connection indicator dot + title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }

                // Simulate mode switch button for user test
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onToggleTestMode)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Simulate Mode",
                        fontSize = 11.sp,
                        color = CyberCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sync metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pending Sync: ",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = if (pendingSyncCount > 0) "$pendingSyncCount Reports" else "All Synced",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pendingSyncCount > 0) ImperialGold else ComplianceGreen
                        )
                    }
                    Text(
                        text = "Last Sync: $lastSyncTime",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Manual Trigger Sync Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CyberCyan.copy(alpha = 0.2f), ImperialGold.copy(alpha = 0.15f))
                            )
                        )
                        .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable(enabled = !isSyncing, onClick = onSyncClick)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = ImperialGold,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync",
                                tint = ImperialGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSyncing) "Syncing..." else "Sync Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}
