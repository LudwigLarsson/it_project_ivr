package com.ludwiglarsson.antiplanner.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkChangeListener(
    private val onConnectionChanged: (isConnected: Boolean) -> Unit
) : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        onConnectionChanged(true)
    }

    override fun onLost(network: Network) {
        onConnectionChanged(false)
    }

    companion object {
        fun register(context: Context, callback: NetworkChangeListener) {
            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
            val manager = context.getSystemService(ConnectivityManager::class.java)
            manager.requestNetwork(networkRequest, callback)
        }

        fun unregister(context: Context, callback: NetworkChangeListener) {
            val manager = context.getSystemService(ConnectivityManager::class.java)
            manager.unregisterNetworkCallback(callback)
        }
    }
}