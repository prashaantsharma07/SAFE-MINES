package com.mine.governance.ui.screens.emergency

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.Severity
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.components.GlassEmergencyButton
import com.mine.governance.ui.components.GlassGoldButton
import com.mine.governance.ui.components.GlassTextField
import com.mine.governance.ui.components.crimsonSosPulse
import com.mine.governance.ui.components.glassEffect
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.ComplianceGreen
import com.mine.governance.ui.theme.CyberCyan
import com.mine.governance.ui.theme.DarkGlassBackgroundBrush
import com.mine.governance.ui.theme.EmergencyCrimson
import com.mine.governance.ui.theme.EmergencyFireBrush
import com.mine.governance.ui.theme.HazardOrange
import com.mine.governance.ui.theme.ImperialGold
import com.mine.governance.ui.theme.MethaneYellow
import com.mine.governance.ui.theme.ObsidianBlack
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmergencyReportScreen(
    viewModel: EmergencyViewModel,
    onNavigateBack: () -> Unit,
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

            // Screen Top Bar
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
                        text = "EMERGENCY REPORT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmergencyCrimson,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Priority 1 • Immediate Subterranean Incident Broadcast",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Live AI Risk Assessment Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderBrush = Brush.linearGradient(
                    listOf(
                        if (uiState.dynamicAiRisk.score >= 80) EmergencyCrimson else ImperialGold,
                        CyberCyan
                    )
                ),
                contentPadding = 14.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🤖 Edge AI Risk Score:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.dynamicAiRisk.score}/100",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.dynamicAiRisk.score >= 80) EmergencyCrimson else ImperialGold
                            )
                        }

                        // Severity Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (uiState.dynamicAiRisk.riskLevel) {
                                        Severity.CRITICAL -> EmergencyCrimson.copy(alpha = 0.25f)
                                        Severity.HIGH -> HazardOrange.copy(alpha = 0.25f)
                                        Severity.MEDIUM -> MethaneYellow.copy(alpha = 0.25f)
                                        Severity.LOW -> ComplianceGreen.copy(alpha = 0.25f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    when (uiState.dynamicAiRisk.riskLevel) {
                                        Severity.CRITICAL -> EmergencyCrimson
                                        Severity.HIGH -> HazardOrange
                                        Severity.MEDIUM -> MethaneYellow
                                        Severity.LOW -> ComplianceGreen
                                    },
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = uiState.dynamicAiRisk.riskLevel.displayName.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.dynamicAiRisk.recommendedImmediateAction,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ImperialGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Emergency Type Selection
            Text(
                text = "1. EMERGENCY TYPE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmergencyType.values().forEach { type ->
                    val isSelected = uiState.selectedType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) ImperialGold.copy(alpha = 0.2f) else CavernDark.copy(alpha = 0.7f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                brush = if (isSelected) Brush.linearGradient(listOf(ImperialGold, CyberCyan))
                                else Brush.linearGradient(listOf(Color.White.copy(0.1f), Color.Transparent)),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectType(type) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = type.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) ImperialGold else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 2: Severity Selection
            Text(
                text = "2. SEVERITY LEVEL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Severity.values().forEach { severity ->
                    val isSelected = uiState.selectedSeverity == severity
                    val severityColor = when (severity) {
                        Severity.CRITICAL -> EmergencyCrimson
                        Severity.HIGH -> HazardOrange
                        Severity.MEDIUM -> MethaneYellow
                        Severity.LOW -> ComplianceGreen
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) severityColor.copy(alpha = 0.25f) else CavernDark)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) severityColor else Color.White.copy(0.1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectSeverity(severity) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = severity.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) severityColor else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 3: Incident Information
            Text(
                text = "3. INCIDENT INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                placeholder = "Incident Title (e.g. Methane Gas Surge at Face 2)",
                label = "Title"
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                placeholder = "Describe nature and cause of the incident...",
                label = "Description",
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassTextField(
                value = uiState.immediateObservations,
                onValueChange = { viewModel.onImmediateObservationsChange(it) },
                placeholder = "Immediate observations (e.g. Gas sensor reading 2.4% CH4)",
                label = "Immediate Observations",
                singleLine = false,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Affected Personnel Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassEffect(shape = RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Affected Miners / Personnel",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Miners inside danger perimeter",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.updatePersonnelCount(uiState.affectedPersonnel - 1) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CavernDark)
                            .border(1.dp, CyberCyan.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = CyberCyan)
                    }

                    Text(
                        text = "${uiState.affectedPersonnel}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ImperialGold,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    IconButton(
                        onClick = { viewModel.updatePersonnelCount(uiState.affectedPersonnel + 1) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CavernDark)
                            .border(1.dp, EmergencyCrimson.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = EmergencyCrimson)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 4: Automatic Telemetry Capture Card
            Text(
                text = "4. AUTOMATIC TELEMETRY CAPTURE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderBrush = Brush.linearGradient(listOf(CyberCyan.copy(0.4f), Color.White.copy(0.1f))),
                contentPadding = 12.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GpsFixed, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GPS: ${uiState.latitude}° N, ${uiState.longitude}° E",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Mine Location: ${uiState.mineSector}",
                        fontSize = 12.sp,
                        color = ImperialGold
                    )
                    Text(
                        text = "Reporting Officer: ${uiState.reportingOfficerName} (${uiState.reportingOfficerId})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 5: Evidence Capture
            Text(
                text = "5. EVIDENCE CAPTURE (${uiState.capturedEvidenceCount} ATTACHED)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Photo button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CavernDark)
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { viewModel.addSimulatedEvidence() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Photo", fontSize = 12.sp, color = TextPrimary)
                    }
                }

                // Video button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CavernDark)
                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { viewModel.addSimulatedEvidence() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Videocam, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Video", fontSize = 12.sp, color = TextPrimary)
                    }
                }

                // Audio Note
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CavernDark)
                        .border(1.dp, ImperialGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable { viewModel.addSimulatedEvidence() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = ImperialGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Audio", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Submission Button: SEND EMERGENCY ALERT
            GlassEmergencyButton(
                text = "🚨 SEND EMERGENCY ALERT",
                onClick = { viewModel.submitEmergencyAlert() },
                isLoading = uiState.isSubmitting
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Emergency Submission Confirmation Dialog
        if (uiState.showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissSuccessDialog() },
                containerColor = CavernDark,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ComplianceGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Emergency Broadcast Logged", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Incident ID: ${uiState.submissionResult?.report?.id}",
                            color = ImperialGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "AI Risk Priority: Priority 1 (Score: ${uiState.submissionResult?.riskAssessment?.score}/100)",
                            color = EmergencyCrimson,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (uiState.submissionResult?.wasQueuedOffline == true) {
                                "Case 3: Stored securely in local SQLite outbox queue. Marked Priority 1 for immediate dispatch on network/gateway reconnection."
                            } else {
                                "Case 1: Transmitted to Central Mine Server. Manager dashboard alert notification triggered."
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.dismissSuccessDialog()
                        onNavigateBack()
                    }) {
                        Text(text = "Acknowledge & Return", color = ImperialGold, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
