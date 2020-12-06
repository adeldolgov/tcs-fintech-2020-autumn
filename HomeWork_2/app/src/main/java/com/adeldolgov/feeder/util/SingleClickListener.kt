package com.adeldolgov.feeder.util

import android.os.SystemClock
import android.view.View


abstract class OnSingleClickListener : View.OnClickListener {

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 550
    }

    private var lastTimeClick: Long = 0
    abstract fun onSingleClick(v: View)

    override fun onClick(v: View) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - lastTimeClick
        lastTimeClick = currentClickTime
        if (elapsedTime <= MIN_CLICK_INTERVAL) return
        onSingleClick(v)
    }
}
