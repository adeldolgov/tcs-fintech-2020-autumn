package com.adeldolgov.feeder.di.module

import android.content.Context
import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.database.AppDatabaseProvider
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.data.server.VkServiceProvider
import com.adeldolgov.feeder.di.provider.VkTokenProvider
import com.adeldolgov.feeder.util.imageloader.GlideImageLoader
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import com.adeldolgov.feeder.util.networkavailability.AppNetworkAvailability
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import com.adeldolgov.feeder.util.preferences.AppSharedPreferences
import com.adeldolgov.feeder.util.preferences.Preferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideNetworkAvailability(context: Context): NetworkAvailability {
        return AppNetworkAvailability(context)
    }

    @Provides
    @Singleton
    fun providePreferences(context: Context): Preferences {
        return AppSharedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideApplicationDatabase(context: Context): AppDatabase {
        return AppDatabaseProvider(context).db
    }

    @Provides
    @Singleton
    fun provideVkToken(preferences: Preferences): VkTokenProvider {
        return VkTokenProvider(preferences)
    }

    @Provides
    @Singleton
    fun provideVkService(vkTokenProvider: VkTokenProvider): VkService {
        return VkServiceProvider(vkTokenProvider).vkService
    }

    @Provides
    @Singleton
    fun provideImageLoader(): ImageLoader {
        return GlideImageLoader()
    }

}