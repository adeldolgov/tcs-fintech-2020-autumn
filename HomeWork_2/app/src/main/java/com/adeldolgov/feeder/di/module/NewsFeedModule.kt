package com.adeldolgov.feeder.di.module

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.repository.NewsFeedRepository
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.di.scope.NewsFeedScope
import com.adeldolgov.feeder.ui.presenter.NewsFeedPresenter
import com.adeldolgov.feeder.util.PostType
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import com.adeldolgov.feeder.util.preferences.Preferences
import com.adeldolgov.feeder.util.timeoutpolicy.CacheTimeoutPolicy
import com.adeldolgov.feeder.util.timeoutpolicy.PreferencesCacheTimeout
import dagger.Module
import dagger.Provides


@Module
class NewsFeedModule {

    @Provides
    @NewsFeedScope
    fun provideCacheTimeoutPolicy(preferences: Preferences): CacheTimeoutPolicy {
        return PreferencesCacheTimeout(preferences)
    }

    @Provides
    @NewsFeedScope
    fun provideNewsFeedRepository(
        vkService: VkService,
        appDatabase: AppDatabase,
        networkAvailability: NetworkAvailability,
        cacheTimeoutPolicy: CacheTimeoutPolicy
    ): NewsFeedRepository {
        return NewsFeedRepository(vkService, appDatabase, PostTypes.of(PostType.NEWS_FEED_POST), networkAvailability, cacheTimeoutPolicy)
    }

    @Provides
    fun provideNewsFeedPresenter(newsFeedRepository: NewsFeedRepository): NewsFeedPresenter {
        return NewsFeedPresenter(newsFeedRepository)
    }
}