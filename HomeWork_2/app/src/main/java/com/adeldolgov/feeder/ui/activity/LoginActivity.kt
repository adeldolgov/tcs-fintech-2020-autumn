package com.adeldolgov.feeder.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.extension.debounceClick
import com.adeldolgov.feeder.util.extension.feederApp
import com.adeldolgov.feeder.util.extension.toast
import com.adeldolgov.feeder.util.preferences.Preferences
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    @Inject
    lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        feederApp().appComponent.inject(this)
        if (VK.isLoggedIn()) {
            startMainActivityAndFinishThis()
        }
        vkLoginBtn.debounceClick {
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS, VKScope.OFFLINE))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                preferences.setVkToken(token.accessToken)
                startMainActivityAndFinishThis()
            }

            override fun onLoginFailed(errorCode: Int) {
                this@LoginActivity.toast(getString(R.string.error_vk_login, errorCode))
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startMainActivityAndFinishThis() {
        startActivity(MainActivity.createIntent(this))
        finish()
    }
}