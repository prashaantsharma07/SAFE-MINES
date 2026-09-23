package com.mine.governance.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.ui.components.ConnectivityCard
import com.mine.governance.ui.components.EmergencySosFloatingButton
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.components.StatCard
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.ComplianceGreen
import com.mine.governance.ui.theme.CyberCyan
import com.mine.governance.ui.theme.DarkGlassBackgroundBrush
import com.mine.governance.ui.theme.EmergencyCrimson
import com.mine.governance.ui.theme.HazardOrange
import com.mine.governance.ui.theme.ImperialGold
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToEmergency: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToObservation: () -> Unit,
    onNavigateToInspection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGlassBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Section: Officer Identity & Shift Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Avatar Glass Circle
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(CavernDark)
                            .border(1.5.dp, ImperialGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Officer Profile",
                            tint = ImperialGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = uiState.currentUser?.name ?: "Rajesh Kumar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${uiState.currentUser?.role ?: "Safety Field Officer"} • ${uiState.currentUser?.assignedMine ?: "Mine B"}",
                            fontSize = 12.sp,
                            color = ImperialGold
                        )
                        Text(
                            text = uiState.shiftTimestamp,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                // Notification Bell Glass Icon
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CavernDark.copy(alpha = 0.8f))
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Connectivity Status Card
            ConnectivityCard(
                networkMode = uiState.networkMode,
                pendingSyncCount = uiState.pendingSyncCount,
                lastSyncTime = uiState.lastSyncTime,
                isSyncing = uiState.isSyncing,
                onSyncClick = { viewModel.triggerSync() },
                onToggleTestMode = { viewModel.toggleNetworkMode() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Statistics Header
            Text(
                text = "SHIFT OPERATIONS OVERVIEW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Statistics Grid (Row 1: Active Tasks & Submitted Reports)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Active Tasks",
                    primaryValue = "${uiState.activeTasksCount}",
                    subLabel = "Critical",
                    subValue = "${uiState.criticalTasksCount}",
                    icon = Icons.Default.Assignment,
                    accentColor = ImperialGold,
                    subValueColor = EmergencyCrimson,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )

                StatCard(
                    title = "Submitted Reports",
                    primaryValue = "${uiState.totalReportsCount}",
                    subLabel = "Open Issues",
                    subValue = "${uiState.openIssuesCount}",
                    icon = Icons.Default.Description,
                    accentColor = CyberCyan,
                    subValueColor = HazardOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Statistics Row 2: Inspection Status Card
            StatCard(
                title = "Mandatory Scheduled Inspections",
                primaryValue = "${uiState.completedInspectionsCount} Completed",
                subLabel = "Pending Inspection Checklists",
                subValue = "${uiState.pendingInspectionsCount} Remaining",
                icon = Icons.Default.Checklist,
                accentColor = ComplianceGreen,
                subValueColor = ImperialGold,
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToInspection
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Action Buttons Title
            Text(
                text = "COMMAND QUICK ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Core Action Buttons
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Action 1: Emergency Report
                QuickActionCard(
                    title = "🚨 Emergency Report",
                    subtitle = "Instant alert for Fire, Gas Leak, or Roof Fall",
                    borderBrush = Brush.linearGradient(listOf(EmergencyCrimson, ImperialGold)),
                    iconTint = EmergencyCrimson,
                    icon = Icons.Default.Warning,
                    onClick = onNavigateToEmergency
                )

                // Action 2: New Observation
                QuickActionCard(
                    title = "New Safety Observation",
                    subtitle = "Log PPE violations, environmental & equipment hazards",
                    borderBrush = Brush.linearGradient(listOf(ImperialGold.copy(0.6f), CyberCyan.copy(0.3f))),
                    iconTint = ImperialGold,
                    icon = Icons.Default.RateReview,
                    onClick = onNavigateToObservation
                )

                // Action 3: Start Inspection
                QuickActionCard(
                    title = "Conduct Scheduled Inspection",
                    subtitle = "Ventilation, safety barrier, and equipment checklists",
                    borderBrush = Brush.linearGradient(listOf(CyberCyan.copy(0.5f), Color.White.copy(0.1f))),
                    iconTint = CyberCyan,
                    icon = Icons.Default.Checklist,
                    onClick = onNavigateToInspection
                )

                // Action 4: View Tasks
                QuickActionCard(
                    title = "Assigned Corrective Tasks",
                    subtitle = "View, execute and submit proof for mine tasks",
                    borderBrush = Brush.linearGradient(listOf(ImperialGold.copy(0.4f), Color.White.copy(0.1f))),
                    iconTint = ImperialGold,
                    icon = Icons.Default.Assignment,
                    onClick = onNavigateToTasks
                )
            }

            // Extra space for floating SOS button and bottom bar
            Spacer(modifier = Modifier.height(100.dp))
        }

        // Permanent floating Emergency SOS Button
        EmergencySosFloatingButton(
            onClick = onNavigateToEmergency,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    borderBrush: Brush,
    iconTint: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderBrush = borderBrush,
        contentPadding = 14.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CavernDark)
                    .border(1.dp, iconTint.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
