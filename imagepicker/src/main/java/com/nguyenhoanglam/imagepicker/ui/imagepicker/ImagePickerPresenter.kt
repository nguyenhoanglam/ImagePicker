package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.listener.OnImageLoaderListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.camera.CameraModule
import com.nguyenhoanglam.imagepicker.ui.camera.DefaultCameraModule
import com.nguyenhoanglam.imagepicker.ui.camera.OnImageReadyListener
import com.nguyenhoanglam.imagepicker.ui.common.BasePresenter
import java.io.File

/**
 * Created by hoanglam on 8/17/17.
 */
class ImagePickerPresenter(private val imageLoader: ImageFileLoader) : BasePresenter<ImagePickerView?>() {

    private val cameraModule: CameraModule = DefaultCameraModule()
    private val handler = Handler(Looper.getMainLooper())

    fun abortLoading() {
        imageLoader.abortLoadImages()
    }

    fun loadImages(isFolderMode: Boolean) {
        if (!isViewAttached) return
        view!!.showLoading(true)
        imageLoader.loadDeviceImages(isFolderMode, object : OnImageLoaderListener {
            override fun onImageLoaded(images: List<Image>, folders: List<Folder>) {
                handler.post {
                    if (isViewAttached) {
                        view!!.showFetchCompleted(images, folders)
                        val isEmpty = folders?.isEmpty() ?: images.isEmpty()
                        if (isEmpty) {
                            view!!.showEmpty()
                        } else {
                            view!!.showLoading(false)
                        }
                    }
                }
            }

            override fun onFailed(throwable: Throwable) {
                handler.post {
                    if (isViewAttached) {
                        view!!.showError(throwable)
                    }
                }
            }
        })
    }

    fun captureImage(activity: Activity, config: Config, requestCode: Int) {
        val context = activity.applicationContext
        val intent = cameraModule.getCameraIntent(activity, config)
        if (intent == null) {
            Toast.makeText(context, context.getString(R.string.imagepicker_error_create_image_file), Toast.LENGTH_LONG).show()
            return
        }
        activity.startActivityForResult(intent, requestCode)
    }

    fun finishCaptureImage(context: Context, data: Intent?, config: Config) {
        cameraModule.getImage(context, object : OnImageReadyListener {
            override fun onImageReady(images: List<Image>) {
                if (!config.isMultipleMode) {
                    view!!.finishPickImages(images)
                } else {
                    view!!.showCapturedImage(images)
                }
            }
        })
    }

    fun onDoneSelectImages(selectedImages: List<Image>) {
        val list = selectedImages.toMutableList();
        if (list != null && list.isNotEmpty()) {
            var i = 0
            while (i < list.size) {
                val (_, _, path) = list[i]
                val file = File(path)
                if (!file.exists()) {
                    list.removeAt(i);
                    i--
                }
                i++
            }
        }
        view!!.finishPickImages(list)
    }

}