package com.nguyenhoanglam.imagepicker.listener

import android.view.View

/**
 * Created by hoanglam on 8/24/16.
 */
interface OnImageClickListener {
    fun onImageClick(view: View, position: Int, isSelected: Boolean): Boolean
}