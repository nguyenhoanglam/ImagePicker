/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nguyenhoanglam.imagepicker.helper.ImageHelper
import com.nguyenhoanglam.imagepicker.model.CallbackStatus
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.Result
import com.nguyenhoanglam.imagepicker.model.SortBy
import com.nguyenhoanglam.imagepicker.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.ref.WeakReference

class ImagePickerViewModel(application: Application) : AndroidViewModel(application) {

    private val contextRef = WeakReference(application.applicationContext)
    private var job: Job? = null

    val result = MutableLiveData(Result(CallbackStatus.IDLE, arrayListOf()))
    lateinit var selectedImages: MutableLiveData<ArrayList<Image>>

    private lateinit var config: ImagePickerConfig

    fun setConfig(config: ImagePickerConfig) {
        this.config = config
        selectedImages = MutableLiveData(ArrayList(config.selectedImages))
    }

    fun getConfig() = config

    fun fetchImages() {
        if (job != null) return

        result.postValue(Result(CallbackStatus.FETCHING, arrayListOf()))
        job = viewModelScope.launch {
            try {
                val images = fetchImagesFromExternalStorage()
                result.postValue(Result(CallbackStatus.SUCCESS, images))
            } catch (e: Exception) {
                result.postValue(Result(CallbackStatus.SUCCESS, arrayListOf()))
            } finally {
                job = null
            }
        }
    }

    private suspend fun fetchImagesFromExternalStorage(): ArrayList<Image> {
        if (contextRef.get() == null) return arrayListOf()

        return withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            val imageCollectionUri = ImageHelper.getImageCollectionUri()
            val sortBy = when (config.imageSort.by) {
                SortBy.DISPLAY_NAME -> MediaStore.Images.Media.DISPLAY_NAME
                SortBy.DATE_MODIFIED -> MediaStore.Images.Media.DATE_MODIFIED
                else -> MediaStore.Images.Media.DATE_ADDED
            }
            val sortOrder = when (config.imageSort.order) {
                SortOrder.DESC -> "DESC"
                else -> "ASC"
            }

            contextRef.get()!!.contentResolver.query(
                imageCollectionUri,
                projection,
                null,
                null,
                "$sortBy $sortOrder"
            )?.use { cursor ->
                val images = arrayListOf<Image>()

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val bucketIdColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn =
                    cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getStringOrNull(nameColumn) ?: ""
                    val bucketId = cursor.getLong(bucketIdColumn)
                    val bucketName = cursor.getStringOrNull(bucketNameColumn) ?: ""

                    val uri = ContentUris.withAppendedId(imageCollectionUri, id)

                    val image = Image(uri, name, bucketId, bucketName)
                    images.add(image)
                }
                cursor.close()
                images
            } ?: throw IOException()
        }
    }
}