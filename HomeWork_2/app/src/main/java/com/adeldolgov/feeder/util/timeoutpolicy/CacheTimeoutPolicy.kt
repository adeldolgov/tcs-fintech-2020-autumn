package com.adeldolgov.feeder.util.timeoutpolicy

interface CacheTimeoutPolicy {

    fun isValid(): Boolean

    fun setLatestTime(time: Long)

    fun getLatestTime(): Long

}