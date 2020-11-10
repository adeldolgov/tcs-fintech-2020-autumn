package com.adeldolgov.feeder.di

import android.content.Context
import com.adeldolgov.feeder.data.database.AppDatabaseImpl
import com.adeldolgov.feeder.data.repository.NewsFeedRepository
import com.adeldolgov.feeder.data.server.VKServiceImpl
import com.adeldolgov.feeder.util.ApplicationPreferences
import com.adeldolgov.feeder.util.timeoutpolicy.PreferencesCacheTimeout

class ApplicationContainer(applicationContext: Context) {
    val preferences = ApplicationPreferences(applicationContext)
    private val networkDataSource by lazy { VKServiceImpl(preferences.getVKToken()!!) }
    private val localDataSource = AppDatabaseImpl(applicationContext)
    private val cacheTimeoutPolicy = PreferencesCacheTimeout(preferences)

    val newsFeedRepository by lazy { NewsFeedRepository(networkDataSource.vkService, localDataSource.db, cacheTimeoutPolicy) }
}
