package com.mine.governance.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.data.local.entity.TaskEntity
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.TaskStatus
import com.mine.governance.ui.components.EmergencySosFloatingButton
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.components.GlassGoldButton
import com.mine.governance.ui.components.GlassSecondaryButton
import com.mine.governance.ui.components.GlassTextField
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.ComplianceGreen
import com.mine.governance.ui.theme.CyberCyan
import com.mine.governance.ui.theme.DarkGlassBackgroundBrush
import com.mine.governance.ui.theme.EmergencyCrimson
import com.mine.governance.ui.theme.HazardOrange
import com.mine.governance.ui.theme.ImperialGold
import com.mine.governance.ui.theme.MethaneYellow
import com.mine.governance.ui.theme.MineShaftNavy
import com.mine.governance.ui.theme.ObsidianBlack
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGlassBackgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CavernDark.copy(alpha = 0.8f))
                        .border(1.dp, ImperialGold.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = ImperialGold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "CORRECTIVE TASKS",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ImperialGold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.filteredTasks.size} tasks in current view",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilterTab.values().forEach { tab ->
                    val isSelected = uiState.selectedTab == tab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) ImperialGold.copy(alpha = 0.25f) else CavernDark)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) ImperialGold else CyberCyan.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectTab(tab) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tab.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ImperialGold else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { viewModel.selectTask(task) }
                    )
                }

                // Space for floating button and bottom padding
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Floating Emergency SOS Button
        EmergencySosFloatingButton(
            onClick = onNavigateToEmergency,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 24.dp)
        )

        // Task Execution Modal Bottom Sheet
        if (uiState.selectedTask != null) {
            val task = uiState.selectedTask!!
            ModalBottomSheet(
                onDismissRequest = { viewModel.selectTask(null) },
                sheetState = sheetState,
                containerColor = CavernDark,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                TaskDetailSheetContent(
                    task = task,
                    afterEvidenceCount = uiState.afterEvidenceUris.size,
                    comments = uiState.officerComments,
                    onCommentsChange = { viewModel.onCommentsChange(it) },
                    onAttachEvidence = { viewModel.attachAfterEvidence() },
                    onStartTask = { viewModel.startTask(task.id) },
                    onCompleteTask = { viewModel.completeTask(task.id) },
                    onClose = { viewModel.selectTask(null) }
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Severity.CRITICAL -> EmergencyCrimson
        Severity.HIGH -> HazardOrange
        Severity.MEDIUM -> MethaneYellow
        Severity.LOW -> ComplianceGreen
    }

    val deadlineText = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(task.deadlineDate))

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = 14.dp,
        borderBrush = Brush.linearGradient(
            listOf(priorityColor.copy(alpha = 0.5f), CyberCyan.copy(alpha = 0.2f))
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Priority Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(priorityColor.copy(alpha = 0.2f))
                        .border(1.dp, priorityColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.priority.displayName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = priorityColor
                    )
                }

                // AI Risk Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI Risk: ",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${task.aiRiskScore}/100",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.aiRiskScore >= 80) EmergencyCrimson else ImperialGold
                    )
                }
            }

            Text(
                text = task.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = task.locationSector,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Due: $deadlineText",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (task.status) {
                                TaskStatus.PENDING -> MethaneYellow.copy(alpha = 0.15f)
                                TaskStatus.IN_PROGRESS -> CyberCyan.copy(alpha = 0.15f)
                                TaskStatus.AWAITING_VERIFICATION -> ImperialGold.copy(alpha = 0.15f)
                                TaskStatus.CLOSED -> ComplianceGreen.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = task.status.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (task.status) {
                            TaskStatus.PENDING -> MethaneYellow
                            TaskStatus.IN_PROGRESS -> CyberCyan
                            TaskStatus.AWAITING_VERIFICATION -> ImperialGold
                            TaskStatus.CLOSED -> ComplianceGreen
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDetailSheetContent(
    task: TaskEntity,
    afterEvidenceCount: Int,
    comments: String,
    onCommentsChange: (String) -> Unit,
    onAttachEvidence: () -> Unit,
    onStartTask: () -> Unit,
    onCompleteTask: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TASK DETAILS & EXECUTION",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ImperialGold
                )
                Text(
                    text = "Ref: ${task.id} • Assigned by ${task.assignedBy}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
            }
        }

        Text(
            text = task.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        // Details card
        GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 12.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Problem Statement:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ImperialGold
                )
                Text(
                    text = task.issueDescription,
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Mandatory Corrective Action:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                Text(
                    text = task.requiredAction,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            }
        }

        // Action State Controller
        when (task.status) {
            TaskStatus.PENDING -> {
                GlassGoldButton(
                    text = "▶ START TASK",
                    onClick = onStartTask,
                    leadingIcon = {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ObsidianBlack)
                    }
                )
            }

            TaskStatus.IN_PROGRESS -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "EVIDENCE UPLOAD & REMARKS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GlassSecondaryButton(
                            text = "Add Proof Photo ($afterEvidenceCount)",
                            onClick = onAttachEvidence,
                            modifier = Modifier.weight(1f),
                            leadingIcon = {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CyberCyan)
                            }
                        )
                    }

                    GlassTextField(
                        value = comments,
                        onValueChange = onCommentsChange,
                        label = "Officer Completion Notes",
                        placeholder = "e.g. Fissures injected with polyurethane resin; anchor load tested to 180 kN.",
                        singleLine = false,
                        maxLines = 3
                    )

                    GlassGoldButton(
                        text = "SUBMIT FOR VERIFICATION",
                        onClick = onCompleteTask,
                        leadingIcon = {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ObsidianBlack)
                        }
                    )
                }
            }

            TaskStatus.AWAITING_VERIFICATION -> {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderBrush = Brush.linearGradient(listOf(ImperialGold, ComplianceGreen)),
                    contentPadding = 14.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AssignmentTurnedIn,
                            contentDescription = null,
                            tint = ImperialGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Awaiting Manager Verification",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Evidence submitted. Mine Manager will verify and close the task.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            TaskStatus.CLOSED -> {
                Text(
                    text = "This task has been verified and closed by Mine Management.",
                    color = ComplianceGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
