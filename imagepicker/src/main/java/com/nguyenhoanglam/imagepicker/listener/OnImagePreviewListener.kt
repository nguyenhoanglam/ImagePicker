/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.listener

import com.nguyenhoanglam.imagepicker.model.Image

interface OnImagePreviewListener {
    fun onShowImagePreview(image: Image)
    fun onHideImagePreview()
}