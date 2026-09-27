package com.mine.governance.ui.screens.map

import android.Manifest
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.mine.governance.data.location.LocationHelper
import com.mine.governance.data.location.LocationStatus
import com.mine.governance.ui.theme.DarkCanvas
import com.mine.governance.ui.theme.DeepWarmStone
import com.mine.governance.ui.theme.DividerBorderDark
import com.mine.governance.ui.theme.OperationalGreen
import com.mine.governance.ui.theme.SubPanelDark
import com.mine.governance.ui.theme.TextPrimary
import com.mine.governance.ui.theme.TextSecondary
import com.mine.governance.ui.theme.WarmActiveBeige
import com.mine.governance.ui.theme.WarningOrange
import kotlinx.coroutines.launch

enum class MapLayerType(val label: String) {
    STREET("🗺️ Street Map"),
    SATELLITE("🛰️ Satellite")
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MineMapScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationHelper = remember { LocationHelper(context) }

    // Live coordinates - default to Jharia Colliery pithead if GPS unavailable
    var userLat by remember { mutableDoubleStateOf(23.7957) }
    var userLon by remember { mutableDoubleStateOf(86.4304) }
    var userAccuracy by remember { mutableFloatStateOf(25.0f) }
    var isGpsLocked by remember { mutableStateOf(false) }
    var isLocating by remember { mutableStateOf(true) }
    var selectedLayer by remember { mutableStateOf(MapLayerType.STREET) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Permission launcher for high-accuracy GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fine || coarse) {
            coroutineScope.launch {
                isLocating = true
                val status = locationHelper.fetchCurrentLocation()
                if (status is LocationStatus.Acquired) {
                    userLat = status.latitude
                    userLon = status.longitude
                    userAccuracy = status.accuracyMeters
                    isGpsLocked = !status.isSubterraneanFallback
                    webViewInstance?.evaluateJavascript(
                        "updatePosition(${userLat}, ${userLon}, ${userAccuracy});",
                        null
                    )
                }
                isLocating = false
            }
        } else {
            isLocating = false
        }
    }

    // Acquire GPS upon entering the screen
    LaunchedEffect(Unit) {
        if (locationHelper.hasLocationPermission()) {
            isLocating = true
            val status = locationHelper.fetchCurrentLocation()
            if (status is LocationStatus.Acquired) {
                userLat = status.latitude
                userLon = status.longitude
                userAccuracy = status.accuracyMeters
                isGpsLocked = !status.isSubterraneanFallback
                webViewInstance?.evaluateJavascript(
                    "updatePosition(${userLat}, ${userLon}, ${userAccuracy});",
                    null
                )
            }
            isLocating = false
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepWarmStone)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Navigation & Concession Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCanvas)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SubPanelDark)
                        .border(1.dp, DividerBorderDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WarmActiveBeige
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "GIS MINE CONCESSION MAP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isGpsLocked) OperationalGreen else WarningOrange)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGpsLocked)
                                "GPS Active • %.4f° N, %.4f° E (±%.0fm)".format(userLat, userLon, userAccuracy)
                            else
                                "Mine Pithead • %.4f° N, %.4f° E".format(userLat, userLon),
                            fontSize = 11.sp,
                            color = WarmActiveBeige
                        )
                    }
                }

                // Layer Toggle Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    MapLayerType.entries.forEach { layer ->
                        val isSelected = selectedLayer == layer
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) WarmActiveBeige else SubPanelDark)
                                .border(1.dp, if (isSelected) WarmActiveBeige else DividerBorderDark, RoundedCornerShape(6.dp))
                                .clickable {
                                    selectedLayer = layer
                                    val layerName = if (layer == MapLayerType.SATELLITE) "satellite" else "street"
                                    webViewInstance?.evaluateJavascript("setMapLayer('$layerName');", null)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = layer.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) DeepWarmStone else TextPrimary
                            )
                        }
                    }
                }
            }

            // Real Leaflet / OpenStreetMap WebView
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    view?.evaluateJavascript("map.invalidateSize();", null)
                                    view?.evaluateJavascript(
                                        "updatePosition(${userLat}, ${userLon}, ${userAccuracy});",
                                        null
                                    )
                                }
                            }
                            webChromeClient = WebChromeClient()
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                builtInZoomControls = false
                                displayZoomControls = false
                            }
                            loadDataWithBaseURL(
                                "https://localhost",
                                buildLeafletHtml(userLat, userLon, userAccuracy),
                                "text/html",
                                "UTF-8",
                                null
                            )
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading Indicator while locking GPS
                if (isLocating) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkCanvas.copy(alpha = 0.9f))
                            .border(1.dp, DividerBorderDark, RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = WarmActiveBeige,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Locating your position...",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Map Floating Controls: Zoom In, Zoom Out, Center on User
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Zoom In
                    IconButton(
                        onClick = { webViewInstance?.evaluateJavascript("zoomIn();", null) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.dp, DividerBorderDark, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = WarmActiveBeige)
                    }

                    // Zoom Out
                    IconButton(
                        onClick = { webViewInstance?.evaluateJavascript("zoomOut();", null) },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkCanvas)
                            .border(1.dp, DividerBorderDark, CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = WarmActiveBeige)
                    }

                    // Center on My Location Button
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                isLocating = true
                                val status = locationHelper.fetchCurrentLocation()
                                if (status is LocationStatus.Acquired) {
                                    userLat = status.latitude
                                    userLon = status.longitude
                                    userAccuracy = status.accuracyMeters
                                    isGpsLocked = !status.isSubterraneanFallback
                                    webViewInstance?.evaluateJavascript(
                                        "updatePosition(${userLat}, ${userLon}, ${userAccuracy});",
                                        null
                                    )
                                } else {
                                    webViewInstance?.evaluateJavascript("centerUser();", null)
                                }
                                isLocating = false
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WarmActiveBeige)
                            .border(1.5.dp, DeepWarmStone, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.MyLocation,
                            contentDescription = "My Location",
                            tint = DeepWarmStone
                        )
                    }
                }
            }
        }
    }
}

/**
 * Builds the interactive HTML document rendering Leaflet.js with OpenStreetMap / Satellite tiles
 * and a pulsing beacon marking the exact user position.
 */
private fun buildLeafletHtml(
    initialLat: Double,
    initialLon: Double,
    accuracyMeters: Float
): String {
    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <title>Mine Officer Map</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <style>
        html, body, #map {
            width: 100%;
            height: 100%;
            margin: 0;
            padding: 0;
            background-color: #1c1917;
            overflow: hidden;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }
        /* Custom pulsating beacon for user location */
        .user-beacon-container {
            position: relative;
            width: 32px;
            height: 32px;
        }
        .user-beacon-pulse {
            position: absolute;
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: rgba(37, 99, 235, 0.45);
            animation: radar-pulse 2s infinite ease-out;
        }
        .user-beacon-dot {
            position: absolute;
            top: 6px;
            left: 6px;
            width: 20px;
            height: 20px;
            border-radius: 50%;
            background: #2563EB;
            border: 3px solid #FFFFFF;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.6);
        }
        @keyframes radar-pulse {
            0% { transform: scale(0.5); opacity: 1; }
            100% { transform: scale(2.5); opacity: 0; }
        }
        .leaflet-popup-content-wrapper {
            background: #292524;
            color: #F8F6F0;
            border: 1px solid #4A3525;
            border-radius: 10px;
            box-shadow: 0 6px 18px rgba(0,0,0,0.6);
        }
        .leaflet-popup-tip {
            background: #292524;
        }
        .leaflet-popup-content {
            margin: 10px 14px;
            font-size: 13px;
            line-height: 1.4;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script>
        var currentLat = $initialLat;
        var currentLon = $initialLon;
        var currentAcc = $accuracyMeters;

        var map = L.map('map', {
            zoomControl: false,
            attributionControl: false
        }).setView([currentLat, currentLon], 16);

        // Standard Street Map Tile Layer (OSM / Google Maps Style)
        var streetLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            subdomains: ['a','b','c']
        }).addTo(map);

        // High-Resolution Satellite Tile Layer
        var satelliteLayer = L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}', {
            maxZoom: 18
        });

        // Pulsating User Location Marker
        var userIcon = L.divIcon({
            className: 'user-beacon-container',
            html: '<div class="user-beacon-pulse"></div><div class="user-beacon-dot"></div>',
            iconSize: [32, 32],
            iconAnchor: [16, 16]
        });

        var userMarker = L.marker([currentLat, currentLon], { icon: userIcon }).addTo(map);
        userMarker.bindPopup("<b>📍 YOU ARE STANDING HERE</b><br>Mine Safety Officer Position<br><span style='color:#E8DEC8;font-size:11px;'>Lat: " + currentLat.toFixed(5) + "° | Lon: " + currentLon.toFixed(5) + "°</span>").openPopup();

        var accuracyCircle = L.circle([currentLat, currentLon], {
            radius: currentAcc,
            color: '#2563EB',
            fillColor: '#3B82F6',
            fillOpacity: 0.15,
            weight: 1
        }).addTo(map);

        function updatePosition(lat, lon, acc) {
            currentLat = lat;
            currentLon = lon;
            currentAcc = acc;
            userMarker.setLatLng([lat, lon]);
            accuracyCircle.setLatLng([lat, lon]);
            accuracyCircle.setRadius(acc);
            userMarker.setPopupContent("<b>📍 YOU ARE STANDING HERE</b><br>Mine Safety Officer Position<br><span style='color:#E8DEC8;font-size:11px;'>Lat: " + lat.toFixed(5) + "° | Lon: " + lon.toFixed(5) + "°</span>");
            map.flyTo([lat, lon], 16, { animate: true, duration: 1.0 });
        }

        function setMapLayer(type) {
            if (type === 'satellite') {
                if (map.hasLayer(streetLayer)) map.removeLayer(streetLayer);
                satelliteLayer.addTo(map);
            } else {
                if (map.hasLayer(satelliteLayer)) map.removeLayer(satelliteLayer);
                streetLayer.addTo(map);
            }
        }

        function zoomIn() { map.zoomIn(); }
        function zoomOut() { map.zoomOut(); }
        function centerUser() { map.flyTo([currentLat, currentLon], 16, { animate: true, duration: 1.0 }); }

        setTimeout(function() {
            map.invalidateSize();
        }, 300);
    </script>
</body>
</html>
    """.trimIndent()
}
