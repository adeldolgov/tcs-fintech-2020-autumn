package com.adeldolgov.feeder.data.server

import com.adeldolgov.feeder.data.pojo.ApiResponse
import com.adeldolgov.feeder.data.pojo.Count
import com.adeldolgov.feeder.data.pojo.NewsFeed
import io.reactivex.Single
import retrofit2.http.*

interface VKService {

    @GET("newsfeed.get?filters=post")
    fun getNewsFeedPosts(@Query("count") count: Int, @Query("start_from") start_from: Int): Single<ApiResponse<NewsFeed>>

    @FormUrlEncoded
    @POST("likes.add?type=post")
    fun addLikeAtPost(@Field("item_id") postId: Long, @Field("owner_id") ownerId: Long): Single<ApiResponse<Count>>

    @FormUrlEncoded
    @POST("likes.delete?type=post")
    fun deleteLikeAtPost(@Field("item_id") id: Long, @Field("owner_id") ownerId: Long): Single<ApiResponse<Count>>

    @FormUrlEncoded
    @POST("newsfeed.ignoreItem?type=wall")
    fun ignorePost(@Field("item_id") id: Long, @Field("owner_id") ownerId: Long): Single<ApiResponse<Int>>

}