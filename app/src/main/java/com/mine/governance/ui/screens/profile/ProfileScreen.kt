package com.mine.governance.ui.screens.profile

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.theme.BrandEarthBrown
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

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onLogoutSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

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

                Text(
                    text = "OFFICER PROFILE & SETTINGS",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Officer Identification Card
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 18.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.5.dp, WarmActiveBeige, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = WarmActiveBeige,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = user?.name ?: "Rajesh Kumar",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = user?.role ?: "Safety Field Officer",
                            fontSize = 13.sp,
                            color = WarmActiveBeige,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SubPanelDark)
                                .border(1.dp, DividerBorderDark, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Employee ID: ${user?.employeeId ?: "EMP-7842"}",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DGMS Certification Details
            Text(text = "STATUTORY CERTIFICATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = OperationalGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "DGMS Statutory Authority", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Text(
                        text = user?.dgmsCertification ?: "DGMS First Class Manager Certificate (CMR-2019/4821)",
                        fontSize = 12.sp,
                        color = WarmActiveBeige
                    )
                    Text(
                        text = "Authorized under Coal Mines Regulations 2017 for underground inspections & statutory closure orders.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Deployment Location
            Text(text = "ASSIGNED DEPLOYMENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProfileInfoRow(icon = Icons.Default.LocationCity, label = "Colliery", value = user?.assignedColliery ?: "Jharia Colliery (BCCL)")
                    ProfileInfoRow(icon = Icons.Default.Security, label = "Assigned Mine", value = user?.assignedMine ?: "Mine B (Underground Shaft 4)")
                    ProfileInfoRow(icon = Icons.Default.Badge, label = "Department", value = user?.department ?: "Safety Operations & Compliance")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Offline Sync Management
            Text(text = "OFFLINE & SYNC MANAGEMENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = 14.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Subterranean Outbox Queue", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text(
                                text = if (uiState.pendingSyncCount > 0) "${uiState.pendingSyncCount} Reports Pending Upload" else "All reports synchronized with cloud",
                                fontSize = 12.sp,
                                color = if (uiState.pendingSyncCount > 0) WarmActiveBeige else OperationalGreen
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SubPanelDark)
                                .border(1.dp, DividerBorderDark, RoundedCornerShape(8.dp))
                                .clickable(enabled = !uiState.isSyncing) { viewModel.triggerSync() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (uiState.isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = WarmActiveBeige, strokeWidth = 2.dp)
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Sync Now", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Text(text = "Last Cloud Sync: ${uiState.lastSyncTime}", fontSize = 11.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secure Logout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCanvas)
                    .border(1.dp, CriticalRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { viewModel.logout(onLogoutSuccess) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = CriticalRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "END SHIFT & SECURE LOGOUT", color = CriticalRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = WarmActiveBeige, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
    }
}
