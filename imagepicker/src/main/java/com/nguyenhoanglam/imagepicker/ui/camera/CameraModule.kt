/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.camera

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.nguyenhoanglam.imagepicker.helper.DeviceHelper
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class CameraModule : Serializable {

    companion object {
        const val SUFFIX = ".jpg"
    }

    private var currentFileUri: Uri? = null
    private var currentFilePath: String? = null
    private var currentFileName: String? = null

    fun getCameraIntent(context: Context, config: Config): Intent? {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val imageFile = try {
            createImageFile(context, config.rootDirectory, config.subDirectory)
        } catch (e: IOException) {
            null
        }

        if (imageFile != null) {
            val providerName = context.packageName + ".fileprovider"
            val imageUri: Uri = FileProvider.getUriForFile(context, providerName, imageFile)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            grantAppPermission(context, intent, imageUri)

            currentFileUri = imageUri
            currentFilePath = imageFile.absolutePath

            return intent
        }

        return null
    }

    @Suppress("DEPRECATION")
    private fun createImageFile(
        context: Context,
        rootDirectory: String,
        subDirectory: String
    ): File? {
        val rootDir: File? =
            if (DeviceHelper.isMinSdk29) context.getExternalFilesDir(rootDirectory)
            else Environment.getExternalStoragePublicDirectory(rootDirectory)

        if (rootDir == null) return null

        val storageDir = File(rootDir, subDirectory)
        var isDirExisted = storageDir.exists()
        if (!isDirExisted) {
            isDirExisted = storageDir.mkdirs()
        }
        if (isDirExisted) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val filePrefix = "IMG_${timeStamp}_"

            currentFileName = filePrefix

            return File.createTempFile(filePrefix, SUFFIX, storageDir)
        }

        return null
    }

    @Suppress("DEPRECATION")
    fun saveImage(
        context: Context,
        config: Config,
        imageReadyListener: OnImageReadyListener
    ) {
        if (currentFileUri == null) {
            imageReadyListener.onImageNotReady()
            reset(context)
        }

        val contentResolver = context.contentResolver;
        try {
            if (DeviceHelper.isMinSdk29) {
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val filePrefix = "IMG_${timeStamp}_"
                val fileName = filePrefix + SUFFIX
                val relativePath = "${config.rootDirectory}/${config.subDirectory}"

                currentFileName = filePrefix

                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                }

                val bitmap = getBitmapFromUri(contentResolver, currentFileUri!!)
                contentResolver.run {
                    val url =
                        if (config.rootDirectory == Config.ROOT_DIR_DOWNLOAD) MediaStore.Downloads.EXTERNAL_CONTENT_URI
                        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                    val newFileUri = contentResolver.insert(url, values)
                    if (newFileUri != null) {
                        val imageOutputStream = openOutputStream(newFileUri)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream)

                        val images = arrayListOf(
                            Image(newFileUri, currentFileName!!, 0, config.subDirectory)
                        )
                        imageReadyListener.onImageReady(images)
                    } else {
                        imageReadyListener.onImageNotReady()
                    }
                    reset(context)
                }
            } else {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(currentFilePath),
                    null
                ) { _, _ ->
                    val images = arrayListOf(
                        Image(currentFileUri!!, currentFileName!!, 0, config.subDirectory)
                    )
                    imageReadyListener.onImageReady(images)
                    reset(context)
                }
            }
        } catch (e: Exception) {
            imageReadyListener.onImageNotReady()
            reset(context)
        }
    }

    private fun reset(context: Context) {
        if (DeviceHelper.isMinSdk29) {
            deleteFileFromUri(context, currentFileUri!!)
        }
        revokeAppPermission(context, currentFileUri!!)
        currentFileUri = null
        currentFilePath = null
        currentFileName = null
    }

    private fun deleteFileFromUri(context: Context, uri: Uri) {
        context.contentResolver.delete(uri, null, null)
    }

    private fun grantAppPermission(context: Context, intent: Intent, fileUri: Uri) {
        val resolvedIntentActivities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolvedIntentInfo in resolvedIntentActivities) {
            val packageName = resolvedIntentInfo.activityInfo.packageName
            context.grantUriPermission(
                packageName,
                fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun revokeAppPermission(context: Context, fileUri: Uri) {
        context.revokeUriPermission(
            fileUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }

    private fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        return bitmap
    }


}