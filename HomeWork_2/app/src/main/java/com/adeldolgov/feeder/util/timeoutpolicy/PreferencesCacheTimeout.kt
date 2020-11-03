package com.adeldolgov.feeder.util.timeoutpolicy

import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.util.ApplicationPreferences

class PreferencesCacheTimeout : CacheTimeoutPolicy {

    companion object {
        private const val CACHE_TIMEOUT = 1000L * 60
    }

    private val appPreferences = ApplicationPreferences(FeederApp.applicationContext())

    override fun isValid(): Boolean {
        return System.currentTimeMillis() - getLatestTime() < CACHE_TIMEOUT
    }

    override fun getLatestTime(): Long = appPreferences.getLatestTimeCacheTimeout()


    override fun setLatestTime(time: Long) {
        appPreferences.setLatestTimeCacheTimeout(time)
    }
}