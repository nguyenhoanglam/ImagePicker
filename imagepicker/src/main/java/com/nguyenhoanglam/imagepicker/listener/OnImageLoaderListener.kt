/*
 * Copyright (C) 2021 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.listener

import com.nguyenhoanglam.imagepicker.model.Image

interface OnImageLoaderListener {
    fun onImageLoaded(images: ArrayList<Image>)
    fun onFailed(throwable: Throwable)
}