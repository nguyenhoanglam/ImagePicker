/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.widget.Toast
import com.nguyenhoanglam.imagepicker.R

object DeviceHelper {
    val isMinSdk29 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private val isMinSdk30 get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

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

    @Suppress("DEPRECATION")
    fun getScreenHeight(activity: Activity): Int {
        return if (isMinSdk30) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }
}