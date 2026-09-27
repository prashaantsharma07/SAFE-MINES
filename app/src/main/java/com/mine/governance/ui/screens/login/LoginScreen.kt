package com.mine.governance.ui.screens.login

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.components.GlassGoldButton
import com.mine.governance.ui.components.GlassTextField
import com.mine.governance.ui.theme.CavernDark
import com.mine.governance.ui.theme.CyberCyan
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DarkGlassBackgroundBrush
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.ImperialGold
import com.mine.governance.ui.theme.ObsidianBlack
import com.mine.governance.ui.theme.SubPanelDark
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary
import com.mine.governance.ui.theme.WarmActiveBeige

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkGlassBackgroundBrush)
    ) {
        // Ambient background subtle light pools
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(CyberCyan.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ImperialGold.copy(alpha = 0.08f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Coal Mine Emblem / Shield Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(ImperialGold.copy(alpha = 0.25f), CavernDark)
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(ImperialGold, CyberCyan)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Coal Mine Safety",
                    tint = ImperialGold,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MINING GOVERNANCE",
                color = ImperialGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )

            Text(
                text = "Subterranean Field Execution Platform",
                color = TextSecondary,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Main Glassmorphic Login Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = 22.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Officer Authentication",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Enter your Coal India credentials",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Employee ID Input
                    GlassTextField(
                        value = uiState.employeeId,
                        onValueChange = { viewModel.onEmployeeIdChange(it) },
                        label = "Employee ID / Username",
                        placeholder = "e.g. EMP-7842",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = ImperialGold
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    GlassTextField(
                        value = uiState.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = "Password",
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = CyberCyan
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = TextSecondary
                                )
                            }
                        },
                        isError = uiState.error != null,
                        errorMessage = uiState.error
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Demo Preset Chips: Officer & Admin
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SubPanelDark)
                                .border(1.dp, DividerBorderDark, RoundedCornerShape(8.dp))
                                .clickable { viewModel.fillDemoOfficer() }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "👤 Field Officer: Rajesh Kumar",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Auto-fill",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmActiveBeige
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SubPanelDark)
                                .border(1.dp, DividerBorderDark, RoundedCornerShape(8.dp))
                                .clickable { viewModel.fillAdminDemo() }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🛡️ Mine Admin: Dr. Vikram Sethi",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Auto-fill",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarmActiveBeige
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign-in Button
                    GlassGoldButton(
                        text = "SECURE SIGN IN",
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = uiState.isLoading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real Functional Biometric Quick Sign-In
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkCanvas)
                            .border(1.dp, WarmActiveBeige.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .clickable(enabled = !uiState.isLoading) {
                                viewModel.loginWithBiometrics(onLoginSuccess)
                            }
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometric Touch ID",
                                tint = WarmActiveBeige,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "1-Tap Biometric Touch Sign-In",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = WarmActiveBeige
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Authorized Personnel Only • Encrypted Offline Sync v1.0",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
