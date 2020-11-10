package com.adeldolgov.feeder.ui.presenter

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView

abstract class RxPresenter<V: MvpView> : MvpPresenter<V>() {
    private val disposables = CompositeDisposable()

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    override fun detachView(view: V) {
        disposables.clear()
        super.detachView(view)
    }

    protected fun removeDisposable(disposable: Disposable?) {
        disposable?.let {
            disposables.remove(it)
        }
    }

    protected fun Disposable.disposeOnFinish(): Disposable {
        disposables += this
        return this
    }

    protected fun dispose(disposable: Disposable) {
        if (!disposables.remove(disposable)) {
            disposable.dispose()
        }
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        add(disposable)
    }
}