package com.mine.governance.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.mine.governance.domain.model.NetworkMode
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class NetworkMonitor(private val context: Context) {

    // Manual override state for testing subterranean conditions
    private val manualOverride = MutableStateFlow<NetworkMode?>(null)

    val networkModeFlow: Flow<NetworkMode> = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        fun updateState() {
            val override = manualOverride.value
            if (override != null) {
                trySend(override)
                return
            }

            if (connectivityManager == null) {
                trySend(NetworkMode.OFFLINE)
                return
            }

            val activeNetwork = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

            val mode = when {
                capabilities == null -> NetworkMode.OFFLINE
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> {
                    // Check if high bandwidth or subterranean mesh Wi-Fi
                    val downstream = capabilities.linkDownstreamBandwidthKbps
                    if (downstream < 300) {
                        NetworkMode.WEAK_NETWORK
                    } else {
                        NetworkMode.ONLINE
                    }
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    // Local mine gateway without full WAN internet access
                    NetworkMode.WEAK_NETWORK
                }
                else -> NetworkMode.OFFLINE
            }
            trySend(mode)
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) = updateState()
            override fun onLost(network: Network) = updateState()
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) = updateState()
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager?.registerNetworkCallback(request, callback)
        updateState()

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }

    fun setManualMode(mode: NetworkMode?) {
        manualOverride.value = mode
    }

    fun toggleNextMode(current: NetworkMode) {
        val next = when (current) {
            NetworkMode.ONLINE -> NetworkMode.WEAK_NETWORK
            NetworkMode.WEAK_NETWORK -> NetworkMode.OFFLINE
            NetworkMode.OFFLINE -> NetworkMode.ONLINE
        }
        manualOverride.value = next
    }
}
