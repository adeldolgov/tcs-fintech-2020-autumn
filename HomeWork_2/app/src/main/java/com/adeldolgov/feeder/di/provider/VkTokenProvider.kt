package com.adeldolgov.feeder.di.provider

import com.adeldolgov.feeder.util.preferences.Preferences

class VkTokenProvider(private val preferences: Preferences) {
    fun provideVkToken(): String {
        return preferences.getVkToken() ?: "EMPTY_TOKEN"
    }
}