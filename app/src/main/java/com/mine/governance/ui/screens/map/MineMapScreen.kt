package com.mine.governance.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mine.governance.domain.model.Severity
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
import com.mine.governance.ui.theme.WarningOrange

data class MineMapPin(
    val id: String,
    val title: String,
    val subtitle: String,
    val sector: String,
    val depth: String,
    val normX: Float, // 0.0 to 1.0 within map canvas
    val normY: Float,
    val severity: Severity?,
    val isOfficerBeacon: Boolean = false,
    val telemetryInfo: String,
    val actionDirective: String
)

enum class MapLayerFilter(val label: String) {
    ALL("All Layers"),
    HAZARDS("Hazards (L1/L2)"),
    DRIFTS("Drifts & Shafts"),
    SENSORS("Atmospheric Sensors"),
    INFRASTRUCTURE("Fans & Pumps")
}

@Composable
fun MineMapScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var selectedPin by remember { mutableStateOf<MineMapPin?>(null) }
    var selectedFilter by remember { mutableStateOf(MapLayerFilter.ALL) }

    val pins = remember {
        listOf(
            MineMapPin(
                id = "PIN-1",
                title = "Methane Gas Surge",
                subtitle = "CH4: 2.8% • AI Risk 92/100",
                sector = "Sector 3 (East Drift)",
                depth = "-380m Subterranean Level",
                normX = 0.68f,
                normY = 0.38f,
                severity = Severity.CRITICAL,
                isOfficerBeacon = false,
                telemetryInfo = "Auxiliary Fan CFM: 1,850 m³/min • Methane Sensor: CH4-03 ACTIVE",
                actionDirective = "Immediate zone isolation per DGMS CMR-2017 Reg. 153. Restrict entry."
            ),
            MineMapPin(
                id = "PIN-2",
                title = "Hydraulic Roof Canopy Stress",
                subtitle = "Pressure Drop: 140 Bar • Risk 67/100",
                sector = "Deep Extraction Face 2",
                depth = "-420m Longwall Advance",
                normX = 0.42f,
                normY = 0.65f,
                severity = Severity.HIGH,
                isOfficerBeacon = false,
                telemetryInfo = "Extensometer: 14mm displacement • Canopy 8A-12B warning",
                actionDirective = "Set secondary timber cogs and verify pump accumulator pressure."
            ),
            MineMapPin(
                id = "PIN-3",
                title = "Main Booster Fan Station #1",
                subtitle = "Airflow: 6.2 m/s • Optimal",
                sector = "North Incline Shaft",
                depth = "-120m Intermediate Split",
                normX = 0.30f,
                normY = 0.25f,
                severity = Severity.LOW,
                isOfficerBeacon = false,
                telemetryInfo = "RPM: 740 • Static Water Gauge: 65mm • Power: Normal Grid",
                actionDirective = "Operational. Next routine vibration logging due at 14:00."
            ),
            MineMapPin(
                id = "PIN-4",
                title = "Emergency Sump Pump 4B",
                subtitle = "Water Head: 14.2 Bar • Flow: Normal",
                sector = "Lower Haulage Level 5",
                depth = "-460m Deep Sump",
                normX = 0.75f,
                normY = 0.78f,
                severity = Severity.LOW,
                isOfficerBeacon = false,
                telemetryInfo = "Float Switch: Active • Discharge rate: 450 gpm",
                actionDirective = "Clear mud sediment strainer during shift handover."
            ),
            MineMapPin(
                id = "PIN-5",
                title = "Officer Rajesh Kumar (Beacon)",
                subtitle = "Safety Field Officer • EMP-7842",
                sector = "Shaft 4 Pithead & Control Station",
                depth = "Surface Incline (+180m MSL)",
                normX = 0.50f,
                normY = 0.15f,
                severity = null,
                isOfficerBeacon = true,
                telemetryInfo = "GPS: 23.7957° N, 86.4304° E • Subterranean Comms: Online",
                actionDirective = "Authorized Level-3 Inspection Authority."
            )
        )
    }

    val visiblePins = remember(selectedFilter, pins) {
        when (selectedFilter) {
            MapLayerFilter.ALL -> pins
            MapLayerFilter.HAZARDS -> pins.filter { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH }
            MapLayerFilter.DRIFTS -> pins.filter { it.isOfficerBeacon }
            MapLayerFilter.SENSORS -> pins.filter { it.title.contains("Gas", ignoreCase = true) || it.title.contains("Stress", ignoreCase = true) }
            MapLayerFilter.INFRASTRUCTURE -> pins.filter { it.title.contains("Fan", ignoreCase = true) || it.title.contains("Pump", ignoreCase = true) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepWarmStone)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(38.dp)
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
                        text = "GIS MINE CONCESSION MAP",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Jharia Colliery • Underground Drift Network & Hazards",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            // Layer Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCanvas)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MapLayerFilter.entries.forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) WarmActiveBeige else SubPanelDark)
                            .border(1.dp, if (isSelected) WarmActiveBeige else DividerBorderDark, RoundedCornerShape(8.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = filter.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DeepWarmStone else TextPrimary
                        )
                    }
                }
            }

            // Interactive Map Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color(0xFF141210))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.6f, 3.5f)
                            offsetX = (offsetX + pan.x).coerceIn(-1000f, 1000f)
                            offsetY = (offsetY + pan.y).coerceIn(-1000f, 1000f)
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val cx = (canvasWidth / 2f) + offsetX
                    val cy = (canvasHeight / 2f) + offsetY

                    // Draw Geological Strata Grid
                    val gridSpacing = 60f * scale
                    val startX = (offsetX % gridSpacing)
                    val startY = (offsetY % gridSpacing)

                    var curX = startX
                    while (curX < canvasWidth) {
                        drawLine(
                            color = Color(0xFF262320),
                            start = Offset(curX, 0f),
                            end = Offset(curX, canvasHeight),
                            strokeWidth = 1f
                        )
                        curX += gridSpacing
                    }

                    var curY = startY
                    while (curY < canvasHeight) {
                        drawLine(
                            color = Color(0xFF262320),
                            start = Offset(0f, curY),
                            end = Offset(canvasWidth, curY),
                            strokeWidth = 1f
                        )
                        curY += gridSpacing
                    }

                    // Draw Jharia Colliery Concession Boundary Polygon
                    val p1 = Offset(cx - 320f * scale, cy - 380f * scale)
                    val p2 = Offset(cx + 340f * scale, cy - 340f * scale)
                    val p3 = Offset(cx + 380f * scale, cy + 360f * scale)
                    val p4 = Offset(cx - 280f * scale, cy + 420f * scale)

                    val boundaryPath = Path().apply {
                        moveTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(p3.x, p3.y)
                        lineTo(p4.x, p4.y)
                        close()
                    }

                    drawPath(
                        path = boundaryPath,
                        color = Color(0xFF4A3525).copy(alpha = 0.18f)
                    )
                    drawPath(
                        path = boundaryPath,
                        color = WarmActiveBeige.copy(alpha = 0.5f),
                        style = Stroke(
                            width = 2f * scale,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                        )
                    )

                    // Draw Subterranean Drift Haulage Tunnels
                    val inclineMain = Path().apply {
                        moveTo(cx + (0.50f - 0.5f) * 600f * scale, cy + (0.15f - 0.5f) * 700f * scale) // Pithead
                        lineTo(cx + (0.30f - 0.5f) * 600f * scale, cy + (0.25f - 0.5f) * 700f * scale) // Intermediate
                        lineTo(cx + (0.68f - 0.5f) * 600f * scale, cy + (0.38f - 0.5f) * 700f * scale) // Sector 3
                        lineTo(cx + (0.42f - 0.5f) * 600f * scale, cy + (0.65f - 0.5f) * 700f * scale) // Face 2
                        lineTo(cx + (0.75f - 0.5f) * 600f * scale, cy + (0.78f - 0.5f) * 700f * scale) // Sump
                    }

                    drawPath(
                        path = inclineMain,
                        color = Color(0xFF93C5FD).copy(alpha = 0.75f),
                        style = Stroke(width = 3.5f * scale)
                    )

                    // Secondary Return Airway
                    val returnAirway = Path().apply {
                        moveTo(cx + (0.30f - 0.5f) * 600f * scale, cy + (0.25f - 0.5f) * 700f * scale)
                        lineTo(cx + (0.15f - 0.5f) * 600f * scale, cy + (0.45f - 0.5f) * 700f * scale)
                        lineTo(cx + (0.42f - 0.5f) * 600f * scale, cy + (0.65f - 0.5f) * 700f * scale)
                    }

                    drawPath(
                        path = returnAirway,
                        color = WarningOrange.copy(alpha = 0.55f),
                        style = Stroke(
                            width = 2f * scale,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)
                        )
                    )
                }

                // Render Interactive Clickable Pins
                Box(modifier = Modifier.fillMaxSize()) {
                    visiblePins.forEach { pin ->
                        val pinColor = when {
                            pin.isOfficerBeacon -> WarmActiveBeige
                            pin.severity == Severity.CRITICAL -> CriticalRed
                            pin.severity == Severity.HIGH -> WarningOrange
                            else -> OperationalGreen
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(pin.id) {
                                    detectTapGestures {
                                        selectedPin = pin
                                    }
                                }
                        ) {
                            // Compute pin position
                            // Map coordinates center
                            val canvasWidth = 1000f
                            val canvasHeight = 1200f

                            val posX = (pin.normX - 0.5f) * 600f * scale + offsetX
                            val posY = (pin.normY - 0.5f) * 700f * scale + offsetY

                            Canvas(
                                modifier = Modifier
                                    .size(44.dp)
                                    .align(Alignment.Center)
                                    .offset(x = posX.dp, y = posY.dp)
                                    .clickable { selectedPin = pin }
                            ) {
                                // Outer Glow Ring
                                drawCircle(
                                    color = pinColor.copy(alpha = 0.35f),
                                    radius = 18f * scale
                                )
                                // Solid Center Pin
                                drawCircle(
                                    color = pinColor,
                                    radius = 9f * scale
                                )
                                // Inner White Dot
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.5f * scale
                                )
                            }
                        }
                    }
                }

                // Map Floating Controls (Zoom in / out / reset)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { scale = (scale * 1.3f).coerceAtMost(3.5f) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.dp, DividerBorderDark, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = WarmActiveBeige)
                    }

                    IconButton(
                        onClick = { scale = (scale / 1.3f).coerceAtLeast(0.6f) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.dp, DividerBorderDark, CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = WarmActiveBeige)
                    }

                    IconButton(
                        onClick = {
                            scale = 1.0f
                            offsetX = 0f
                            offsetY = 0f
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.dp, DividerBorderDark, CircleShape)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "Reset", tint = WarmActiveBeige)
                    }
                }
            }

            // Bottom Selected Pin Details Sheet
            AnimatedVisibility(
                visible = selectedPin != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                if (selectedPin != null) {
                    val pin = selectedPin!!
                    val pinColor = when {
                        pin.isOfficerBeacon -> WarmActiveBeige
                        pin.severity == Severity.CRITICAL -> CriticalRed
                        pin.severity == Severity.HIGH -> WarningOrange
                        else -> OperationalGreen
                    }

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentPadding = 16.dp
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(pinColor)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = pin.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }

                                IconButton(
                                    onClick = { selectedPin = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Sector: ${pin.sector}", fontSize = 12.sp, color = WarmActiveBeige, fontWeight = FontWeight.Medium)
                                Text(text = pin.depth, fontSize = 11.sp, color = TextMuted)
                            }

                            Text(
                                text = "Telemetry: ${pin.telemetryInfo}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SubPanelDark)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Directive: ${pin.actionDirective}",
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
