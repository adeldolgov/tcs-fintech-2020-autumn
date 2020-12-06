package com.adeldolgov.feeder.ui.activity

import android.content.IntentFilter
import androidx.annotation.LayoutRes
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.util.extension.initNetworkReceiver
import com.adeldolgov.feeder.util.extension.registerLocalReceiver
import com.adeldolgov.feeder.util.extension.unregisterLocalReceiver
import moxy.MvpAppCompatActivity

open class BaseMvpActivity(@LayoutRes layoutRes: Int) : MvpAppCompatActivity(layoutRes) {

    private val networkReceiver = initNetworkReceiver()

    override fun onStart() {
        super.onStart()
        registerLocalReceiver(networkReceiver, IntentFilter(FeederApp.NETWORK_AVAILABILITY_INTENT_FILTER))
    }

    override fun onStop() {
        super.onStop()
        unregisterLocalReceiver(networkReceiver)
    }
}