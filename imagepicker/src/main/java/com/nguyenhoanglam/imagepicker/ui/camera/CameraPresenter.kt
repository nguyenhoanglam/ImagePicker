package com.nguyenhoanglam.imagepicker.ui.camera

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.common.BasePresenter

/**
 * Created by hoanglam on 8/22/17.
 */
class CameraPresenter : BasePresenter<CameraView?>() {

    private val cameraModule: CameraModule = DefaultCameraModule()

    fun captureImage(activity: Activity, config: Config, requestCode: Int) {
        val context = activity.applicationContext
        val intent = cameraModule.getCameraIntent(activity, config)
        if (intent == null) {
            Toast.makeText(context, context.getString(R.string.imagepicker_error_create_image_file), Toast.LENGTH_LONG).show()
            return
        }
        activity.startActivityForResult(intent, requestCode)
    }

    fun finishCaptureImage(context: Context, config: Config?) {
        cameraModule.getImage(context, object : OnImageReadyListener {
            override fun onImageReady(images: List<Image>) {
                view!!.finishPickImages(images)
            }
        })
    }
}