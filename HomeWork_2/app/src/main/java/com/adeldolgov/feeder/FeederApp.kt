package com.adeldolgov.feeder

import android.app.Application
import com.adeldolgov.feeder.di.component.*
import com.adeldolgov.feeder.di.module.AppModule

class FeederApp : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance: FeederApp
        const val NETWORK_AVAILABILITY_INTENT_FILTER = "feeder_network_filter"
        const val NETWORK_AVAILABILITY_EXTRA = "network_result_extra"
    }

    lateinit var appComponent: AppComponent
    var newsFeedComponent: NewsFeedComponent? = null
    var postDetailComponent: PostDetailComponent? = null
    var profileComponent: ProfileComponent? = null


    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun addNewsFeedComponent() {
        newsFeedComponent = DaggerNewsFeedComponent.builder().appComponent(appComponent).build()
    }

    fun clearNewsFeedComponent() {
        newsFeedComponent = null
    }

    fun addPostDetailComponent() {
        postDetailComponent = DaggerPostDetailComponent.builder().appComponent(appComponent).build()
    }

    fun clearPostDetailComponent() {
        postDetailComponent = null
    }

    fun addProfileComponent() {
        profileComponent = DaggerProfileComponent.builder().appComponent(appComponent).build()
    }

    fun clearProfileComponent() {
        profileComponent = null
    }

}