package com.adeldolgov.feeder.util.timeoutpolicy

import com.adeldolgov.feeder.util.ApplicationPreferences

class PreferencesCacheTimeout(private val applicationPreferences: ApplicationPreferences) :
    CacheTimeoutPolicy {

    companion object {
        private const val CACHE_TIMEOUT = 1000L * 60
    }

    override fun isValid(): Boolean {
        return System.currentTimeMillis() - getLatestTime() < CACHE_TIMEOUT
    }

    override fun getLatestTime(): Long = applicationPreferences.getLatestTimeCacheTimeout()


    override fun setLatestTime(time: Long) {
        applicationPreferences.setLatestTimeCacheTimeout(time)
    }
}