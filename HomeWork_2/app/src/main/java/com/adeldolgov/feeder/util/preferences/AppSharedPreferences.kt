package com.adeldolgov.feeder.util.preferences

import android.content.Context

class AppSharedPreferences(val context: Context): Preferences {
    companion object {
        const val PREFERENCE_NAME = "settings"
        private const val TOKEN_KEY = "token_preference"
        private const val LATEST_TIME_CACHE_TIMEOUT = "cache_timeout_preference"
    }

    override fun setVkToken(token: String) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            .putString(TOKEN_KEY, token).apply()
    }

    override fun getVkToken(): String? {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, "EMPTY_TOKEN")
    }

    override fun setLatestTimeCacheTimeout(timeout: Long) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            .putLong(LATEST_TIME_CACHE_TIMEOUT, timeout).apply()
    }

    override fun getLatestTimeCacheTimeout(): Long {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getLong(LATEST_TIME_CACHE_TIMEOUT, 0)
    }

}