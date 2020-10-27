package com.adeldolgov.feeder.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.ApplicationPreferences
import com.adeldolgov.feeder.util.feederApp
import com.adeldolgov.feeder.util.toast
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
            feederApp().setVKTokenForSession(ApplicationPreferences(this).getVKToken())
            startMainActivity()
        }
        vkLoginBtn.setOnClickListener {
            VK.login(this, arrayListOf(VKScope.WALL, VKScope.PHOTOS, VKScope.FRIENDS))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                ApplicationPreferences(this@LoginActivity).setVKToken(token.accessToken)
                feederApp().setVKTokenForSession(token.accessToken)
                startMainActivity()
            }

            override fun onLoginFailed(errorCode: Int) {
                this@LoginActivity.toast(getString(R.string.erro_vk_login, errorCode))
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}