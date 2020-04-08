package com.nguyenhoanglam.imagepicker.ui.camera

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.nguyenhoanglam.imagepicker.helper.ImageHelper.grantAppPermission
import com.nguyenhoanglam.imagepicker.helper.ImageHelper.revokeAppPermission
import com.nguyenhoanglam.imagepicker.helper.ImageHelper.singleListFromPath
import com.nguyenhoanglam.imagepicker.helper.LogHelper.Companion.instance
import com.nguyenhoanglam.imagepicker.model.Config
import java.io.File
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hoanglam on 8/18/17.
 */
class DefaultCameraModule : CameraModule, Serializable {

    private var imageFilePath: String? = null

    override fun getCameraIntent(context: Context, config: Config): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = createImageFileUri(context, config.directoryName)
        instance?.d("Created image URI $uri")
        if (uri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            grantAppPermission(context, intent, uri)
            return intent
        }
        return null
    }

    private fun createImageFileUri(context: Context, directory: String): Uri? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val filePath = "DCIM/$directory"
        val fileName = "IMG_$timeStamp.jpg"
        val uri: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath)
            uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageFilePath = uri!!.path
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val imageFile = File(dir, "/$directory/$fileName")
            val parentFile = imageFile.parentFile
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs()
            }
            val appContext = context.applicationContext
            val providerName = appContext.packageName + ".fileprovider"
            uri = FileProvider.getUriForFile(appContext, providerName, imageFile)
            imageFilePath = imageFile.absolutePath
        }
        return uri
    }

    override fun getImage(context: Context, imageReadyListener: OnImageReadyListener?) {
        checkNotNull(imageReadyListener) { "OnImageReadyListener must not be null" }
        if (imageFilePath == null) {
            imageReadyListener.onImageReady(arrayListOf())
            return
        }
        if (imageFilePath != null) {
            MediaScannerConnection.scanFile(context.applicationContext, arrayOf(imageFilePath!!), null
            ) { path, uri ->
                var path = path
                if (path != null) {
                    path = imageFilePath
                }
                imageReadyListener.onImageReady(singleListFromPath(path!!))
                revokeAppPermission(context, uri)
                imageFilePath = null
            }
        }
    }
}