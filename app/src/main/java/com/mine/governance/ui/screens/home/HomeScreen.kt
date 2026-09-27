package com.mine.governance.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.mine.governance.ui.theme.BrandEarthBrown
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.CriticalRed
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DeepWarmStone
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.OperationalGreen
import com.mine.governance.ui.theme.SubPanelDark
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary
import com.mine.governance.ui.theme.WarmActiveBeige
import com.mine.governance.ui.theme.WarningOrange

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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.simulationMessage) {
        val msg = uiState.simulationMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSimulationMessage()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepWarmStone)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Section: Officer / Admin Identity & Shift Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.5.dp, if (uiState.isAdmin) WarningOrange else WarmActiveBeige, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = "User Profile",
                            tint = if (uiState.isAdmin) WarningOrange else WarmActiveBeige,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.currentUser?.name ?: if (uiState.isAdmin) "Dr. Vikram Sethi (Director)" else "Rajesh Kumar",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (uiState.isAdmin) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(WarningOrange.copy(alpha = 0.2f))
                                        .border(1.dp, WarningOrange, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(text = "ADMIN", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = WarningOrange)
                                }
                            }
                        }
                        Text(
                            text = "${uiState.currentUser?.role ?: if (uiState.isAdmin) "Chief Safety Director" else "Safety Field Officer"} • ${uiState.currentUser?.assignedMine ?: "Mine B"}",
                            fontSize = 12.sp,
                            color = WarmActiveBeige
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
                        .background(DarkCanvas)
                        .border(1.dp, DividerBorderDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = WarmActiveBeige,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ACTIVE SIMULATION DRILL ALERT BANNER
            AnimatedVisibility(visible = uiState.isSimulationDrillActive) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderBrush = Brush.linearGradient(listOf(CriticalRed, WarningOrange)),
                        surfaceTint = Color(0xFF381A1A),
                        contentPadding = 14.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CriticalRed))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "EMERGENCY DRILL SIMULATION ACTIVE",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CriticalRed,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CriticalRed)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "LIVE DRILL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Text(
                                text = "Scenario #9021: 2.8% CH4 Methane Surge at Face 2 • 2 Priority Emergency Tasks Dispatched.",
                                fontSize = 12.sp,
                                color = WarmActiveBeige
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connectivity Status Card
            ConnectivityCard(
                networkMode = uiState.networkMode,
                pendingSyncCount = uiState.pendingSyncCount,
                lastSyncTime = uiState.lastSyncTime,
                isSyncing = uiState.isSyncing,
                onSyncClick = { viewModel.triggerSync() },
                onToggleTestMode = { viewModel.toggleNetworkMode() }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // SIMULATION MODE TOGGLE CONTROLLER
            // ==========================================
            Text(
                text = "SAFETY DRILL SIMULATION CONTROLLER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 14.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.isSimulationDrillActive) "Simulation Drill Running" else "Emergency Drill Simulator",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isSimulationDrillActive) CriticalRed else TextPrimary
                            )
                            Text(
                                text = if (uiState.isSimulationDrillActive)
                                    "Tap button below to end drill and restore normal data."
                                else
                                    "Simulates live CH4 gas incident & registers emergency tasks to demonstrate app response.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (uiState.isSimulationDrillActive)
                                    Brush.linearGradient(listOf(Color(0xFF591C1C), CriticalRed))
                                else
                                    Brush.linearGradient(listOf(BrandEarthBrown, Color(0xFF332316)))
                            )
                            .border(1.dp, if (uiState.isSimulationDrillActive) CriticalRed else WarmActiveBeige.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleSimulationDrill() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isSimulationDrillActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.isSimulationDrillActive) "⏹️ STOP DRILL & RESTORE NORMAL STATE" else "⚡ START EMERGENCY DRILL SIMULATION",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Statistics Header
            Text(
                text = if (uiState.isAdmin) "DGMS CONCESSION EXECUTIVE OVERSIGHT" else "SHIFT OPERATIONS OVERVIEW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Statistics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = if (uiState.isAdmin) "Active Escalations" else "Active Tasks",
                    primaryValue = "${uiState.activeTasksCount}",
                    subLabel = "Critical",
                    subValue = "${uiState.criticalTasksCount}",
                    icon = Icons.AutoMirrored.Filled.Assignment,
                    accentColor = WarmActiveBeige,
                    subValueColor = CriticalRed,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTasks
                )

                StatCard(
                    title = if (uiState.isAdmin) "Logged Incidents" else "Submitted Reports",
                    primaryValue = "${uiState.totalReportsCount}",
                    subLabel = "Pending Sync",
                    subValue = "${uiState.pendingSyncCount}",
                    icon = Icons.Default.Description,
                    accentColor = OperationalGreen,
                    subValueColor = WarningOrange,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToObservation
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = if (uiState.isAdmin) "DGMS Compliance" else "Completed Inspections",
                    primaryValue = if (uiState.isAdmin) "94.2%" else "${uiState.completedInspectionsCount}",
                    subLabel = if (uiState.isAdmin) "Rating" else "Pending",
                    subValue = if (uiState.isAdmin) "Grade A" else "${uiState.pendingInspectionsCount}",
                    icon = Icons.Default.Checklist,
                    accentColor = OperationalGreen,
                    subValueColor = WarmActiveBeige,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToInspection
                )

                StatCard(
                    title = "Open Hazard Issues",
                    primaryValue = "${uiState.openIssuesCount}",
                    subLabel = "Action Req.",
                    subValue = "2",
                    icon = Icons.Default.ReportProblem,
                    accentColor = CriticalRed,
                    subValueColor = CriticalRed,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToObservation
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Actions Header
            Text(
                text = "COMMAND QUICK ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Priority Action 1: Emergency Report Card
            HomeActionCard(
                title = "Emergency Report (Priority 1)",
                subtitle = "Subterranean SOS, explosive gas, fire, or strata ground fall alert",
                borderBrush = Brush.linearGradient(listOf(CriticalRed.copy(0.7f), Color.White.copy(0.1f))),
                iconTint = CriticalRed,
                icon = Icons.Default.Warning,
                onClick = onNavigateToEmergency
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Priority Action 2: Safety Observation
            HomeActionCard(
                title = "New Safety Observation",
                subtitle = "Log workplace hazard, PPE non-compliance, with DGMS AI analysis",
                borderBrush = Brush.linearGradient(listOf(WarmActiveBeige.copy(0.4f), Color.White.copy(0.1f))),
                iconTint = WarmActiveBeige,
                icon = Icons.Default.RateReview,
                onClick = onNavigateToObservation
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Priority Action 3: Corrective Tasks
            HomeActionCard(
                title = "Corrective Tasks Execution",
                subtitle = "View, execute and submit proof for mine tasks",
                borderBrush = Brush.linearGradient(listOf(WarmActiveBeige.copy(0.4f), Color.White.copy(0.1f))),
                iconTint = WarmActiveBeige,
                icon = Icons.AutoMirrored.Filled.Assignment,
                onClick = onNavigateToTasks
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Priority Action 4: GIS Mine Concession Map
            HomeActionCard(
                title = "Interactive GIS Concession Map",
                subtitle = "View drift levels, hazard clusters, and officer beacons",
                borderBrush = Brush.linearGradient(listOf(Color(0xFF93C5FD).copy(0.4f), Color.White.copy(0.1f))),
                iconTint = Color(0xFF93C5FD),
                icon = Icons.Default.Map,
                onClick = onNavigateToInspection
            )

            Spacer(modifier = Modifier.height(90.dp))
        }

        // Floating SOS Button (Quick access across dashboard)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        ) {
            EmergencySosFloatingButton(onClick = onNavigateToEmergency)
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }
}

@Composable
fun HomeActionCard(
    title: String,
    subtitle: String,
    borderBrush: Brush,
    iconTint: Color,
    icon: ImageVector,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        borderBrush = borderBrush,
        contentPadding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkCanvas)
                    .border(1.dp, DividerBorderDark, RoundedCornerShape(10.dp)),
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
