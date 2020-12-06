package com.adeldolgov.feeder.di.component

import com.adeldolgov.feeder.data.database.AppDatabase
import com.adeldolgov.feeder.data.server.VkService
import com.adeldolgov.feeder.di.module.AppModule
import com.adeldolgov.feeder.ui.activity.LoginActivity
import com.adeldolgov.feeder.ui.activity.PostDetailActivity
import com.adeldolgov.feeder.ui.dialogfragment.NewPostBottomSheetDialogFragment
import com.adeldolgov.feeder.ui.fragment.NewsFeedFragment
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import com.adeldolgov.feeder.util.networkavailability.NetworkAvailability
import com.adeldolgov.feeder.util.preferences.Preferences

import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun inject(loginActivity: LoginActivity)

    fun inject(detailActivity: PostDetailActivity)

    fun inject(newsFeedFragment: NewsFeedFragment)
    
    fun inject(dialogFragment: NewPostBottomSheetDialogFragment)

    fun providePreferences(): Preferences

    fun provideVkService(): VkService

    fun provideAppDatabase(): AppDatabase

    fun provideImageLoader(): ImageLoader

    fun provideNetworkAvailability(): NetworkAvailability

}