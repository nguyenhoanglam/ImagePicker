package com.nguyenhoanglam.imagepicker.ui.imagepicker

import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.common.MvpView

/**
 * Created by hoanglam on 8/17/17.
 */
interface ImagePickerView : MvpView {
    fun showLoading(isLoading: Boolean)
    fun showFetchCompleted(images: List<Image>, folders: List<Folder>)
    fun showError(throwable: Throwable)
    fun showEmpty()
    fun showCapturedImage(images: List<Image>)
    fun finishPickImages(images: List<Image>)
}