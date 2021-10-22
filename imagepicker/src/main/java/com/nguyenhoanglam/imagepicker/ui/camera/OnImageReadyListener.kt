/*
 * Copyright (C) 2021 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.camera

import com.nguyenhoanglam.imagepicker.model.Image

interface OnImageReadyListener {
    fun onImageReady(images: ArrayList<Image>)
    fun onImageNotReady()
}