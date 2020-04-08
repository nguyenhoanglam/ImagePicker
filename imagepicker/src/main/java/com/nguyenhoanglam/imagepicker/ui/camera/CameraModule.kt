package com.nguyenhoanglam.imagepicker.ui.camera

import android.content.Context
import android.content.Intent
import com.nguyenhoanglam.imagepicker.model.Config

/**
 * Created by hoanglam on 8/18/17.
 */
interface CameraModule {
    fun getCameraIntent(context: Context, config: Config): Intent?
    fun getImage(context: Context, imageReadyListener: OnImageReadyListener?)
}