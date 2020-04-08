package com.nguyenhoanglam.imagepicker.listener

import com.nguyenhoanglam.imagepicker.model.Image

/**
 * Created by hoanglam on 8/18/17.
 */
interface OnImageSelectionListener {
    fun onSelectionUpdate(images: List<Image>)
}