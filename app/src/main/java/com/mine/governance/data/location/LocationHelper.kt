package com.mine.governance.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class LocationStatus {
    object Idle : LocationStatus()
    object Locating : LocationStatus()
    data class Acquired(
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Float,
        val isSubterraneanFallback: Boolean = false,
        val timestamp: Long = System.currentTimeMillis()
    ) : LocationStatus()
    object PermissionDenied : LocationStatus()
    data class Unavailable(val reason: String) : LocationStatus()
}

class LocationHelper(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _status = MutableStateFlow<LocationStatus>(LocationStatus.Idle)
    val status: StateFlow<LocationStatus> = _status.asStateFlow()

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun fetchCurrentLocation(): LocationStatus {
        if (!hasLocationPermission()) {
            _status.value = LocationStatus.PermissionDenied
            return LocationStatus.PermissionDenied
        }

        _status.value = LocationStatus.Locating

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            val location: Location? = suspendCancellableCoroutine { cont ->
                fusedClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { loc ->
                    if (cont.isActive) cont.resume(loc)
                }.addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }

                cont.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }

            if (location != null) {
                val acquired = LocationStatus.Acquired(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracyMeters = location.accuracy,
                    isSubterraneanFallback = false
                )
                _status.value = acquired
                acquired
            } else {
                // Try last known location
                val lastLoc: Location? = suspendCancellableCoroutine { cont ->
                    fusedClient.lastLocation.addOnSuccessListener { loc ->
                        if (cont.isActive) cont.resume(loc)
                    }.addOnFailureListener {
                        if (cont.isActive) cont.resume(null)
                    }
                }

                if (lastLoc != null) {
                    val acquired = LocationStatus.Acquired(
                        latitude = lastLoc.latitude,
                        longitude = lastLoc.longitude,
                        accuracyMeters = lastLoc.accuracy,
                        isSubterraneanFallback = false
                    )
                    _status.value = acquired
                    acquired
                } else {
                    // Under ground shafts, satellite signals are physically blocked by rock strata.
                    // Fall back to Jharia Colliery, Shaft 4 surveyed pithead coordinates.
                    val fallback = LocationStatus.Acquired(
                        latitude = 23.7957,
                        longitude = 86.4304,
                        accuracyMeters = 50.0f,
                        isSubterraneanFallback = true
                    )
                    _status.value = fallback
                    fallback
                }
            }
        } catch (e: Exception) {
            val fallback = LocationStatus.Acquired(
                latitude = 23.7957,
                longitude = 86.4304,
                accuracyMeters = 50.0f,
                isSubterraneanFallback = true
            )
            _status.value = fallback
            fallback
        }
    }

    fun setPermissionDenied() {
        _status.value = LocationStatus.PermissionDenied
    }
}
