package com.adeldolgov.feeder.di.module

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.repository.ProfileRepository
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.di.scope.PostDetailsScope
import com.adeldolgov.feeder.ui.presenter.ProfilePresenter
import com.adeldolgov.feeder.util.PostType
import com.adeldolgov.feeder.util.PostTypes
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import dagger.Module
import dagger.Provides


@Module
class ProfileModule {

    @Provides
    @PostDetailsScope
    fun provideProfileRepository(
        vkService: VkService,
        appDatabase: AppDatabase,
        networkAvailability: NetworkAvailability
    ): ProfileRepository {
        return ProfileRepository(vkService, appDatabase, PostTypes.of(PostType.WALL_POST), networkAvailability)
    }

    @Provides
    fun provideProfilePresenter(profileRepository: ProfileRepository): ProfilePresenter {
        return ProfilePresenter(profileRepository)
    }
}