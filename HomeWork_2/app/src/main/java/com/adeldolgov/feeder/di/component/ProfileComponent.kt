package com.adeldolgov.feeder.di.component

import com.adeldolgov.feeder.di.module.ProfileModule
import com.adeldolgov.feeder.di.scope.PostDetailsScope
import com.adeldolgov.feeder.ui.presenter.ProfilePresenter
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [ProfileModule::class])
@PostDetailsScope
interface ProfileComponent {
    fun provideProfilePresenter(): ProfilePresenter
}