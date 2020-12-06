package com.adeldolgov.feeder.util.preferences

interface Preferences {
    fun getVkToken(): String?
    fun setVkToken(token: String)
    fun getLatestTimeCacheTimeout(): Long
    fun setLatestTimeCacheTimeout(timeout: Long)
}