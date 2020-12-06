package com.adeldolgov.feeder.di.component

import com.adeldolgov.feeder.di.module.PostDetailModule
import com.adeldolgov.feeder.di.scope.PostDetailsScope
import com.adeldolgov.feeder.ui.presenter.PostDetailPresenter
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [PostDetailModule::class])
@PostDetailsScope
interface PostDetailComponent {
    fun providePostDetailPresenter(): PostDetailPresenter
}