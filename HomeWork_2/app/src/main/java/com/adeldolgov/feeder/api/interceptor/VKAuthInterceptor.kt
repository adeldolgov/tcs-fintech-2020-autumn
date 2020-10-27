package com.adeldolgov.feeder.api.interceptor

import com.adeldolgov.feeder.util.Session
import okhttp3.Interceptor

class VKAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val httpUrlWithToken = originalRequest.url().newBuilder()
            .addQueryParameter("access_token", Session.VK_TOKEN)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(httpUrlWithToken).build())
    }
}
