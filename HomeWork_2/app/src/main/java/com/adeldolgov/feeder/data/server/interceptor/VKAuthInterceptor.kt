package com.adeldolgov.feeder.data.server.interceptor

import okhttp3.Interceptor

class VKAuthInterceptor(private val vkToken: String) : Interceptor {

    companion object {
        private const val VK_API_VERSION = "5.124"
    }

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originalRequest = chain.request()
        val httpUrlWithToken = originalRequest.url().newBuilder()
            .addQueryParameter("access_token", vkToken)
            .addQueryParameter("v", VK_API_VERSION)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(httpUrlWithToken).build())
    }
}
