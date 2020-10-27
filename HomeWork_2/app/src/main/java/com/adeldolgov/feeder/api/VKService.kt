package com.adeldolgov.feeder.api

import com.adeldolgov.feeder.api.interceptor.VKAuthInterceptor
import com.adeldolgov.feeder.data.pojo.ApiResponse
import com.adeldolgov.feeder.data.pojo.Count
import com.adeldolgov.feeder.data.pojo.NewsFeed
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface VKService {

    @GET("newsfeed.get?filters=post&v=5.133")
    fun getNewsFeedPosts(): Single<ApiResponse<NewsFeed>>

    @FormUrlEncoded
    @POST("likes.add?v=5.124&type=post")
    fun addLikeAtPost(@Field("item_id") postId: Long, @Field("owner_id") ownerId: Long): Single<ApiResponse<Count>>

    @FormUrlEncoded
    @POST("likes.delete?v=5.124&type=post")
    fun deleteLikeAtPost(@Field("item_id") id: Long, @Field("owner_id") ownerId: Long): Single<ApiResponse<Count>>

    @FormUrlEncoded
    @POST("newsfeed.ignoreItem?v=5.124&type=wall")
    fun ignorePost(@Field("item_id") id: Long, @Field("owner_id") ownerId: Long): Single<ApiResponse<Int>>

    companion object RetrofitBuilder {
        private const val BASE_URL = "https://api.vk.com/method/"

        private fun getRetrofit(): Retrofit {

            val client = OkHttpClient.Builder()
                .addInterceptor(VKAuthInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }
        val vkService: VKService = getRetrofit().create(VKService::class.java)
    }
}