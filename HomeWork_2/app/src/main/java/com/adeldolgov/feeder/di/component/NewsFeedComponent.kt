package com.adeldolgov.feeder.di.component

import com.adeldolgov.feeder.di.module.NewsFeedModule
import com.adeldolgov.feeder.di.scope.NewsFeedScope
import com.adeldolgov.feeder.ui.presenter.NewsFeedPresenter
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [NewsFeedModule::class])
@NewsFeedScope
interface NewsFeedComponent {
    fun provideNewsFeedPresenter(): NewsFeedPresenter
}