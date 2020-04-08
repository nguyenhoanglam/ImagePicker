package com.nguyenhoanglam.imagepicker.ui.common

/**
 * Created by hoanglam on 8/17/17.
 */
open class BasePresenter<T : MvpView?> {
    var view: T? = null
        private set

    fun attachView(view: T) {
        this.view = view
    }

    fun detachView() {
        view = null
    }

    protected val isViewAttached: Boolean
        protected get() = view != null
}