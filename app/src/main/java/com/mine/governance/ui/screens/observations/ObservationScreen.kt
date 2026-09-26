package com.mine.governance.ui.screens.observations

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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import com.mine.governance.domain.model.Severity
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.components.GlassGoldButton
import com.mine.governance.ui.components.GlassSecondaryButton
import com.mine.governance.ui.components.GlassTextField
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
fun ObservationScreen(
    viewModel: ObservationViewModel,
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
            viewModel.addPhoto(currentPhotoUri.toString())
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val savedPath = CameraHelper.saveContentUriToEvidenceFile(context, uri)
            viewModel.addPhoto(savedPath ?: uri.toString())
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

            // Screen Header
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
                        text = "SAFETY OBSERVATION",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Non-emergency hazard reporting & DGMS analysis",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Field inputs
            Text(text = "OBSERVATION DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            GlassTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = "Title",
                placeholder = "e.g. Loose Strata & Defective Idler Roller"
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassTextField(
                value = uiState.sector,
                onValueChange = { viewModel.onSectorChange(it) },
                label = "Location / Sector",
                placeholder = "e.g. Sector 3 • East Drift Haulage"
            )

            Spacer(modifier = Modifier.height(10.dp))

            GlassTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                label = "Hazard Description",
                placeholder = "Describe the observed hazard, machinery sounds, or condition in detail...",
                singleLine = false,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Photo Evidence Section
            Text(
                text = "EVIDENCE PHOTOS (${uiState.attachedPhotos.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Camera Button
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

            // Attached photo previews
            if (uiState.attachedPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.attachedPhotos.forEachIndexed { index, uriStr ->
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, DividerBorderDark, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = uriStr,
                                contentDescription = "Attached photo",
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { viewModel.removePhoto(index) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(22.dp)
                                    .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Gemini AI Analysis Trigger
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SubPanelDark)
                    .border(1.dp, WarmActiveBeige.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable(enabled = !uiState.isAnalyzing && uiState.description.isNotBlank()) {
                        viewModel.analyzeWithGemini()
                    }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isAnalyzing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = WarmActiveBeige, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Analyzing with DGMS Regulatory AI...", color = WarmActiveBeige, fontSize = 13.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Analyze with DGMS / Gemini AI", color = WarmActiveBeige, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // AI Analysis Results Card
            if (uiState.aiAnalysis != null) {
                val analysis = uiState.aiAnalysis!!
                Spacer(modifier = Modifier.height(14.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = 14.dp
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = analysis.category,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            val sevColor = when (analysis.severity) {
                                Severity.CRITICAL -> CriticalRed
                                Severity.HIGH -> WarningOrange
                                Severity.MEDIUM -> WarningOrange
                                Severity.LOW -> OperationalGreen
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(sevColor.copy(alpha = 0.2f))
                                    .border(1.dp, sevColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = analysis.severity.displayName.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sevColor
                                )
                            }
                        }

                        Text(
                            text = "Regulation: ${analysis.dgmsRegulation}",
                            fontSize = 12.sp,
                            color = WarmActiveBeige,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "Action: ${analysis.recommendedAction}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Text(
                            text = if (analysis.isLiveGemini) "Verified via Gemini 1.5 Flash" else "Evaluated via DGMS Coal Mines Regulations Heuristic",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            GlassGoldButton(
                text = "LOG SAFETY OBSERVATION",
                onClick = { viewModel.submitObservation() },
                isLoading = uiState.isSubmitting,
                enabled = uiState.description.isNotBlank()
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Success Dialog
        if (uiState.submissionSuccessMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.clearSuccessMessage() },
                containerColor = DarkCanvas,
                shape = RoundedCornerShape(16.dp),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OperationalGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Observation Logged", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Text(text = uiState.submissionSuccessMessage!!, color = TextSecondary, fontSize = 13.sp)
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearSuccessMessage()
                        onNavigateBack()
                    }) {
                        Text(text = "Done", color = WarmActiveBeige, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
