package com.mine.governance.ui.screens.emergency

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mine.governance.data.camera.CameraHelper
import com.mine.governance.data.location.LocationStatus
import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.Severity
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.components.GlassEmergencyButton
import com.mine.governance.ui.components.GlassTextField
import com.mine.governance.ui.components.glassEffect
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.CriticalRed
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DeepWarmStone
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.EmergencyCrimson
import com.mine.governance.ui.theme.OperationalGreen
import com.mine.governance.ui.theme.SubPanelDark
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary
import com.mine.governance.ui.theme.WarmActiveBeige
import com.mine.governance.ui.theme.WarningOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmergencyReportScreen(
    viewModel: EmergencyViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            viewModel.addEvidencePhoto(currentPhotoUri.toString())
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val savedPath = CameraHelper.saveContentUriToEvidenceFile(context, uri)
            viewModel.addEvidencePhoto(savedPath ?: uri.toString())
        }
    }

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.requestRealLocation()
        } else {
            viewModel.onLocationPermissionDenied()
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
                        .background(DarkCanvas)
                        .border(1.dp, DividerBorderDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WarmActiveBeige
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "EMERGENCY REPORT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CriticalRed,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Priority 1 • Subterranean Hazard Alert",
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
                        if (uiState.dynamicAiRisk.score >= 80) CriticalRed else WarmActiveBeige,
                        DividerBorderDark
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
                                text = "⚡ DGMS Risk Score:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.dynamicAiRisk.score}/100",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (uiState.dynamicAiRisk.score >= 80) CriticalRed else WarmActiveBeige
                            )
                        }

                        // Severity Tag
                        val sevColor = when (uiState.dynamicAiRisk.riskLevel) {
                            Severity.CRITICAL -> CriticalRed
                            Severity.HIGH -> WarningOrange
                            Severity.MEDIUM -> WarningOrange
                            Severity.LOW -> OperationalGreen
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(sevColor.copy(alpha = 0.25f))
                                .border(1.dp, sevColor, RoundedCornerShape(6.dp))
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
                        color = WarmActiveBeige
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
                EmergencyType.entries.forEach { type ->
                    val isSelected = uiState.selectedType == type
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) WarmActiveBeige.copy(alpha = 0.2f) else DarkCanvas
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) WarmActiveBeige else DividerBorderDark,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.selectType(type) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = type.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) WarmActiveBeige else TextSecondary
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
                Severity.entries.forEach { severity ->
                    val isSelected = uiState.selectedSeverity == severity
                    val severityColor = when (severity) {
                        Severity.CRITICAL -> CriticalRed
                        Severity.HIGH -> WarningOrange
                        Severity.MEDIUM -> WarningOrange
                        Severity.LOW -> OperationalGreen
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) severityColor.copy(alpha = 0.25f) else DarkCanvas)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) severityColor else DividerBorderDark,
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
                            .background(DarkCanvas)
                            .border(1.dp, DividerBorderDark, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = WarmActiveBeige)
                    }

                    Text(
                        text = "${uiState.affectedPersonnel}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WarmActiveBeige,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    IconButton(
                        onClick = { viewModel.updatePersonnelCount(uiState.affectedPersonnel + 1) },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.dp, CriticalRed.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = CriticalRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 4: Telemetry & Location Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "4. TELEMETRY & GPS POSITION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                // Refresh Location button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SubPanelDark)
                        .clickable {
                            requestLocationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Acquire GPS", fontSize = 11.sp, color = WarmActiveBeige)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 12.dp
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GpsFixed, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = String.format("GPS: %.4f° N, %.4f° E", uiState.latitude, uiState.longitude),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }

                    if (uiState.isSubterraneanLocationFallback) {
                        Text(
                            text = "Signal: Underground strata fallback (Shaft 4 Surveyed Pithead)",
                            fontSize = 11.sp,
                            color = WarningOrange
                        )
                    } else if (uiState.accuracyMeters > 0) {
                        Text(
                            text = "Signal: Real satellite lock (Accuracy: ±${uiState.accuracyMeters.toInt()}m)",
                            fontSize = 11.sp,
                            color = OperationalGreen
                        )
                    }

                    Text(
                        text = "Sector: ${uiState.mineSector}",
                        fontSize = 12.sp,
                        color = WarmActiveBeige
                    )
                    Text(
                        text = "Reporting Officer: ${uiState.reportingOfficerName} (${uiState.reportingOfficerId})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 5: Real Evidence Capture
            Text(
                text = "5. EVIDENCE CAPTURE (${uiState.capturedEvidenceUris.size} ATTACHED)",
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
                // Real Camera Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCanvas)
                        .border(1.dp, DividerBorderDark, RoundedCornerShape(10.dp))
                        .clickable {
                            val (uri, _) = CameraHelper.createEvidencePhotoUri(context)
                            currentPhotoUri = uri
                            takePictureLauncher.launch(uri)
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Camera", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }

                // Gallery Button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCanvas)
                        .border(1.dp, DividerBorderDark, RoundedCornerShape(10.dp))
                        .clickable {
                            pickImageLauncher.launch("image/*")
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Gallery", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Thumbnail list
            if (uiState.capturedEvidenceUris.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.capturedEvidenceUris.forEachIndexed { index, uriStr ->
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, DividerBorderDark, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = uriStr,
                                contentDescription = "Evidence thumbnail",
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { viewModel.removeEvidence(index) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
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
                containerColor = DarkCanvas,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OperationalGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Emergency Broadcast Logged", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Incident ID: ${uiState.submissionResult?.report?.id}",
                            color = WarmActiveBeige,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "DGMS Risk Priority: Priority 1 (Score: ${uiState.submissionResult?.riskAssessment?.score}/100)",
                            color = CriticalRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (uiState.submissionResult?.wasQueuedOffline == true) {
                                "Stored securely in local SQLite outbox queue. Marked Priority 1 for immediate dispatch on network/gateway reconnection."
                            } else {
                                "Transmitted to Central Mine Server. Manager dashboard alert notification triggered."
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
                        Text(text = "Acknowledge & Return", color = WarmActiveBeige, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
