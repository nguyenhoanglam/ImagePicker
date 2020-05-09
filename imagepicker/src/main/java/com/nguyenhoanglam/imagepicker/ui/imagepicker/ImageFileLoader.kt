/*
 * Copyright (c) 2020 Nguyen Hoang Lam.
 * All rights reserved.
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.content.Context
import android.provider.MediaStore
import com.nguyenhoanglam.imagepicker.listener.OnImageLoaderListener
import com.nguyenhoanglam.imagepicker.model.Image
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ImageFileLoader(private val context: Context) {

    private val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

    private val executorService = Executors.newSingleThreadExecutor()
    private val futures = arrayListOf<Future<*>>()

    fun loadDeviceImages(listener: OnImageLoaderListener) {
        val future = executorService.submit(ImageLoadRunnable((listener)))
        futures.add(future)
    }

    fun abortLoadImages() {
        for (future in futures) {
            future.cancel(true)
        }
        futures.clear()
    }

    private inner class ImageLoadRunnable(private val listener: OnImageLoaderListener) : Runnable {
        override fun run() {
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_ADDED)
            if (cursor == null) {
                listener.onFailed(NullPointerException())
                return
            }
            val images = arrayListOf<Image>()
            if (cursor.moveToLast()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val bucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                    val bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val file = makeSafeFile(path)
                    if (file != null && file.exists()) {
                        val image = Image(id, name, path, bucketId, bucketName)
                        images.add(image)
                    }
                } while (cursor.moveToPrevious())
            }
            cursor.close()
            listener.onImageLoaded(images)
        }

    }

    companion object {
        private fun makeSafeFile(path: String?): File? {
            return if (path == null || path.isEmpty()) {
                null
            } else try {
                File(path)
            } catch (ignored: Exception) {
                null
            }
        }
    }

}