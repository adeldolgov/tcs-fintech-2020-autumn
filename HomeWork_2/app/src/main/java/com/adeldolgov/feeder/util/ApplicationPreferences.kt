package com.adeldolgov.feeder.util

import android.content.Context

class ApplicationPreferences(val context: Context) {
    companion object {
        const val PREFERENCE_NAME = "settings"
        private const val TOKEN_KEY = "token_preference"
        private const val LATEST_TIME_CACHE_TIMEOUT = "cache_timeout_preference"
    }

    fun setVKToken(token: String) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            .putString(TOKEN_KEY, token).apply()
    }

    fun getVKToken(): String? {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, null)
    }

    fun setLatestTimeCacheTimeout(timeout: Long) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            .putLong(LATEST_TIME_CACHE_TIMEOUT, timeout).apply()
    }

    fun getLatestTimeCacheTimeout(): Long {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getLong(LATEST_TIME_CACHE_TIMEOUT, 0)
    }
}