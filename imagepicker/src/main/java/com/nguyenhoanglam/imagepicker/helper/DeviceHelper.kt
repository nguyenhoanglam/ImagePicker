/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import com.nguyenhoanglam.imagepicker.R

object DeviceHelper {
    val isMinSdk29 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    val isMinSdk33 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    fun checkCameraAvailability(context: Context): Boolean {
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            val appContext = context.applicationContext
            Toast.makeText(
                appContext,
                appContext.getString(R.string.imagepicker_error_no_camera),
                Toast.LENGTH_LONG
            ).show()

            return false
        }

        return true
    }
}