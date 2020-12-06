package com.adeldolgov.feeder.util

sealed class LoadState {
    class Empty(val message: String): LoadState()
    class Loading : LoadState()
    class Error(val message: String) : LoadState()
}
