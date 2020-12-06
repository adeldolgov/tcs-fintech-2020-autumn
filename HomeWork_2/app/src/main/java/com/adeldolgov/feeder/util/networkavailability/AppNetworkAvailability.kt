package com.adeldolgov.feeder.util.networkavailability

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.adeldolgov.feeder.FeederApp

class AppNetworkAvailability(private val context: Context): NetworkAvailability {

    var networkAvailable: Boolean = false
    private val broadcaster = LocalBroadcastManager.getInstance(context)

    init {
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(
            NetworkRequest.Builder().build(), object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    networkAvailable = true
                    sendNetworkStatus(networkAvailable)
                }

                override fun onLost(network: Network) {
                    networkAvailable = false
                    sendNetworkStatus(networkAvailable)
                }
            })
    }

    private fun sendNetworkStatus(isNetworkAvailable: Boolean) {
        broadcaster.sendBroadcast(
            Intent(FeederApp.NETWORK_AVAILABILITY_INTENT_FILTER)
                .putExtra(FeederApp.NETWORK_AVAILABILITY_EXTRA, isNetworkAvailable)
        )
    }

    override fun isNetworkAvailable(): Boolean {
        return networkAvailable
    }
}