/*
 * Copyright (C) 2021 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.listener

import com.nguyenhoanglam.imagepicker.model.Image

interface OnImageSelectListener {
    fun onSelectedImagesChanged(selectedImages: ArrayList<Image>)
    fun onSingleModeImageSelected(image: Image)
}