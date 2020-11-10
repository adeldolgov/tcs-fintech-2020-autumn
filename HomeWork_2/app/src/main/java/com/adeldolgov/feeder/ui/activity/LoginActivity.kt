package com.adeldolgov.feeder.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.feeder.FeederApp
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.extension.toast
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (VK.isLoggedIn()) {
            startMainActivityAndFinishThis()
        }
        vkLoginBtn.setOnClickListener {
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                FeederApp.instance.applicationContainer.preferences.setVKToken(token.accessToken)
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