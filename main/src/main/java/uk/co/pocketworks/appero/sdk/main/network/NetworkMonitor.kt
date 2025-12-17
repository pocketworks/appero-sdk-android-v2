//
//  NetworkMonitor.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger

/**
 * Internal class for monitoring network connectivity state.
 * Provides a reactive StateFlow<Boolean> for connectivity status.
 */
internal class NetworkMonitor(context: Context) {
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val isConnectedState = MutableStateFlow(checkInitialConnectivity())
    val isConnected: StateFlow<Boolean> = isConnectedState.asStateFlow()

    var forceOfflineMode: Boolean = false
        set(value) {
            field = value
            updateConnectivityState()
        }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateConnectivityState()
        }

        override fun onLost(network: Network) {
            updateConnectivityState()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            updateConnectivityState()
        }
    }

    init {
        registerNetworkCallback()
    }

    /**
     * Checks the initial connectivity state.
     */
    private fun checkInitialConnectivity(): Boolean {
        if (forceOfflineMode) return false

        return try {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } catch (e: Exception) {
            ApperoLogger.log("Error checking initial connectivity: ${e.message}")
            // Default to connected if we can't determine
            true
        }
    }

    /**
     * Updates the connectivity state based on current network status.
     */
    private fun updateConnectivityState() {
        val connected = if (forceOfflineMode) {
            false
        } else {
            checkInitialConnectivity()
        }

        isConnectedState.value = connected
    }

    /**
     * Registers the network callback to monitor connectivity changes.
     */
    private fun registerNetworkCallback() {
        try {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        } catch (e: Exception) {
            ApperoLogger.log("Error registering network callback: ${e.message}")
            // If registration fails, we'll use the initial connectivity state
        }
    }

    /**
     * Unregisters the network callback. Should be called on cleanup.
     */
    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            ApperoLogger.log("Error unregistering network callback: ${e.message}")
            // Ignore errors during cleanup
        }
    }
}
