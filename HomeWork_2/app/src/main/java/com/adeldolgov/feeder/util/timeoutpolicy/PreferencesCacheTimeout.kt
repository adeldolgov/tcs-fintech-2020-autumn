package com.adeldolgov.feeder.util.timeoutpolicy

import com.adeldolgov.feeder.util.preferences.Preferences
import javax.inject.Inject

class PreferencesCacheTimeout @Inject constructor(private val preferences: Preferences) :
    CacheTimeoutPolicy {

    companion object {
        private const val CACHE_TIMEOUT = 1000L * 60
    }

    override fun isValid(): Boolean {
        return System.currentTimeMillis() - getLatestTime() < CACHE_TIMEOUT
    }

    override fun getLatestTime(): Long = preferences.getLatestTimeCacheTimeout()


    override fun setLatestTime(time: Long) {
        preferences.setLatestTimeCacheTimeout(time)
    }
}