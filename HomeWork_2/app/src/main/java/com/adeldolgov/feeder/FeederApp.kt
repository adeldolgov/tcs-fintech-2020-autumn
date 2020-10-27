package com.adeldolgov.feeder

import android.app.Application
import com.adeldolgov.feeder.util.Session

class FeederApp : Application() {
    fun setVKTokenForSession(token: String?) {
        Session.VK_TOKEN = token
    }
}