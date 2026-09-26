package com.mine.governance.ui.screens.map

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.mine.governance.ui.components.GlassCard
import com.mine.governance.ui.theme.CriticalRed
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DeepWarmStone
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.OperationalGreen
import com.mine.governance.ui.theme.TextMuted
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary
import com.mine.governance.ui.theme.WarmActiveBeige
import com.mine.governance.ui.theme.WarningOrange

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MineMapScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val htmlContent = remember { generateLeafletMapHtml() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepWarmStone)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
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

            // Legend Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCanvas)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CriticalRed))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Critical (L2)", fontSize = 11.sp, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarningOrange))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Warning (L1)", fontSize = 11.sp, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(OperationalGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Operational", fontSize = 11.sp, color = TextPrimary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(WarmActiveBeige))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Officer Beacon", fontSize = 11.sp, color = TextPrimary)
                }
            }

            // Leaflet WebView
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL(
                                "https://unpkg.com",
                                htmlContent,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun generateLeafletMapHtml(): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html { margin: 0; padding: 0; height: 100%; width: 100%; background: #1C1917; font-family: sans-serif; }
                #map { height: 100%; width: 100%; background: #1C1917; }
                .leaflet-popup-content-wrapper {
                    background: #292524 !important;
                    color: #FDFBF7 !important;
                    border: 1px solid #44403C;
                    border-radius: 8px;
                    font-size: 12px;
                }
                .leaflet-popup-tip { background: #292524 !important; }
                .badge-critical { color: #DC2626; font-weight: bold; }
                .badge-warning { color: #D97706; font-weight: bold; }
                .badge-safe { color: #16A34A; font-weight: bold; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                // Initialize map centered at Jharia Coalfield, Pit 4
                var map = L.map('map', {
                    zoomControl: false,
                    attributionControl: false
                }).setView([23.7957, 86.4304], 14);

                L.control.zoom({ position: 'topright' }).addTo(map);

                // OpenStreetMap Tile Layer (Dark styled via CSS filter)
                var osmLayer = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 18
                }).addTo(map);

                // Concession Boundary (Jharia Mine B Block)
                var concessionPolygon = L.polygon([
                    [23.8050, 86.4180],
                    [23.8080, 86.4420],
                    [23.7880, 86.4480],
                    [23.7840, 86.4220]
                ], {
                    color: '#E8DEC8',
                    weight: 2,
                    dashArray: '5, 5',
                    fillColor: '#4A3525',
                    fillOpacity: 0.15
                }).addTo(map);
                concessionPolygon.bindPopup("<b>Jharia Colliery Concession</b><br>Mine B Subterranean Block");

                // Subterranean Incline Roadway / Conveyor Line
                var mainHaulageRoad = L.polyline([
                    [23.7990, 86.4280],
                    [23.7957, 86.4304],
                    [23.7920, 86.4330],
                    [23.7890, 86.4360]
                ], {
                    color: '#93C5FD',
                    weight: 3,
                    opacity: 0.8
                }).addTo(map);
                mainHaulageRoad.bindPopup("<b>Main Trunk Haulage Drift</b><br>Inclination: 1 in 4.5 (-380m Level)");

                // Custom Pin Generator
                function createMarker(lat, lng, color, title, desc, badgeClass) {
                    var circle = L.circleMarker([lat, lng], {
                        radius: 8,
                        fillColor: color,
                        color: '#FFFFFF',
                        weight: 2,
                        opacity: 1,
                        fillOpacity: 0.95
                    }).addTo(map);
                    circle.bindPopup("<b>" + title + "</b><br><span class='" + badgeClass + "'>" + desc + "</span>");
                }

                // Markers based on coal mine operations
                createMarker(23.7957, 86.4304, '#E8DEC8', 'Officer Rajesh Kumar (Beacon)', 'Current Sector: Shaft 4 Pithead', 'badge-warning');
                createMarker(23.7985, 86.4340, '#DC2626', 'Hazard: Methane Gas Surge', 'AI Risk: 92/100 [CRITICAL]<br>Sector 3 East Drift', 'badge-critical');
                createMarker(23.7915, 86.4280, '#D97706', 'Hazard: Hydraulic Canopy Tension', 'Risk: 67/100 [WARNING]<br>Deep Extraction Face 2', 'badge-warning');
                createMarker(23.8010, 86.4260, '#16A34A', 'Operational: Main Booster Fan', 'Airflow: 6.2 m/s [NORMAL]', 'badge-safe');
                createMarker(23.7885, 86.4370, '#16A34A', 'Operational: Sump Pump 4B', 'Pressure: 14.2 Bar [STABLE]', 'badge-safe');
            </script>
        </body>
        </html>
    """.trimIndent()
}
