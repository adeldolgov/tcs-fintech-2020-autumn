package com.adeldolgov.feeder.util

import android.content.Context

class ApplicationPreferences(val context: Context) {
    companion object {
        const val PREFERENCE_NAME = "settings"
        const val TOKEN_KEY = "token_preference"
    }

    fun setVKToken(token: String) {
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit()
            .putString(TOKEN_KEY, token).apply()
    }

    fun getVKToken(): String? {
        return context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, null)
    }
}