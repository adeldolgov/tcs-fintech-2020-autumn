package com.adeldolgov.feeder

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import com.adeldolgov.feeder.util.Session

class FeederApp : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: FeederApp? = null
        var isNetworkAvailable: Boolean = false
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
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


    fun setVKTokenForSession(token: String?) {
        Session.VK_TOKEN = token
    }
}