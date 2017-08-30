package com.nguyenhoanglam.imagepicker.ui.common;

/**
 * Created by hoanglam on 8/17/17.
 */

public class BasePresenter<T extends MvpView> {

    private T view;

    public void attachView(T view) {
        this.view = view;
    }

    public T getView() {
        return view;
    }

    public void detachView() {
        view = null;
    }

    protected boolean isViewAttached() {
        return view != null;
    }

}
