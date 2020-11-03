package com.adeldolgov.feeder.data.server.interceptor

import com.adeldolgov.feeder.util.Session
import okhttp3.Interceptor

class VKAuthInterceptor : Interceptor {

    companion object {
        private const val VK_API_VERSION = "5.124"
    }

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val httpUrlWithToken = originalRequest.url().newBuilder()
            .addQueryParameter("access_token", Session.VK_TOKEN)
            .addQueryParameter("v", VK_API_VERSION)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(httpUrlWithToken).build())
    }
}
