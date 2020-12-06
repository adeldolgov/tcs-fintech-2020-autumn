package com.adeldolgov.feeder.di.module

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.repository.PostDetailRepository
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.di.scope.PostDetailsScope
import com.adeldolgov.feeder.ui.presenter.PostDetailPresenter
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import dagger.Module
import dagger.Provides


@Module
class PostDetailModule {

    @Provides
    @PostDetailsScope
    fun providePostDetailRepository(
        vkService: VkService,
        appDatabase: AppDatabase,
        networkAvailability: NetworkAvailability
        ): PostDetailRepository {
        return PostDetailRepository(vkService, appDatabase, networkAvailability)
    }

    @Provides
    fun providePostDetailPresenter(postDetailRepository: PostDetailRepository): PostDetailPresenter {
        return PostDetailPresenter(postDetailRepository)
    }
}