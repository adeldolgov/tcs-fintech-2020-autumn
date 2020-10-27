package com.adeldolgov.feeder.util

sealed class Resource<out T>(open val data: T?) {
    class Loading<out T>(override val data: T?) : Resource<T>(data)
    class Success<out T>(override val data: T) : Resource<T>(data)
    class Empty<out T> : Resource<T>(null)
    class Failure<out T>(override val data: T?, val message: String) : Resource<T>(data)
}
