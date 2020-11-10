package com.adeldolgov.feeder

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.adeldolgov.feeder.di.ApplicationContainer

class FeederApp : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: FeederApp
    }

    var isNetworkAvailable: Boolean = false
    lateinit var applicationContainer: ApplicationContainer

    override fun onCreate() {
        super.onCreate()
        applicationContainer = ApplicationContainer(this)
        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isNetworkAvailable = true
                }

                override fun onLost(network: Network) {
                    isNetworkAvailable = false
                }
            })
    }


}