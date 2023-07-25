/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.content.Context
import android.widget.Toast

class ToastHelper {
    companion object {
        var toast: Toast? = null

        fun show(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
            toast = if (toast == null) {
                Toast.makeText(context.applicationContext, text, duration)
            } else {
                toast?.cancel()
                Toast.makeText(context.applicationContext, text, duration)
            }
            toast?.show()
        }
    }
}