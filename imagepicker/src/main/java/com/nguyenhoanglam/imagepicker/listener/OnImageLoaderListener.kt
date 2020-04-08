package com.nguyenhoanglam.imagepicker.listener

import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image

/**
 * Created by hoanglam on 8/17/17.
 */
interface OnImageLoaderListener {
    fun onImageLoaded(images: List<Image>, folders: List<Folder>)
    fun onFailed(throwable: Throwable)
}