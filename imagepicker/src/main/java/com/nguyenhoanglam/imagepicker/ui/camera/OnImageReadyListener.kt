package com.nguyenhoanglam.imagepicker.ui.camera

import com.nguyenhoanglam.imagepicker.model.Image

interface OnImageReadyListener {
    fun onImageReady(images: List<Image>)
}