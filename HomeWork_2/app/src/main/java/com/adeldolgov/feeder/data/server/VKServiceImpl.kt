package com.adeldolgov.feeder.data.server

import com.adeldolgov.feeder.data.server.interceptor.VKAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class VKServiceImpl {

    companion object {
        private const val BASE_URL = "https://api.vk.com/method/"
    }

    val vkService = initVkRetrofitService()

    private fun initVkRetrofitService(): VKService {
        val client = createOkHttpClient()
        val vkRetrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        return vkRetrofit.create(VKService::class.java)
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(VKAuthInterceptor())
            .build()
    }

}