/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */
package com.nguyenhoanglam.imagepicker.helper

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.IOException

object ExifHelper {
    fun rotateBitmap(photoPath: String?, bitmap: Bitmap): Bitmap {
        if (photoPath == null) return bitmap

        try {
            val ei = ExifInterface(photoPath)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            if (orientation == 1) {
                return bitmap
            }

            val matrix = Matrix()
            when (orientation) {
                2 -> matrix.setScale(-1f, 1f)
                3 -> matrix.setRotate(180f)
                4 -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }

                5 -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }

                6 -> matrix.setRotate(90f)
                7 -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }

                8 -> matrix.setRotate(-90f)
                else -> return bitmap
            }

            return try {
                val orientedBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                bitmap.recycle()
                orientedBitmap
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                bitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }
}