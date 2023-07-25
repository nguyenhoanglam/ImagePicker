/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.net.Uri
import android.provider.MediaStore
import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image

enum class SelectionState {
    NOT_SELECTED,
    SELECTED,
}

object ImageHelper {

    fun singleListFromImage(image: Image): ArrayList<Image> {
        val images = arrayListOf<Image>()
        images.add(image)
        return images
    }

    fun folderListFromImages(images: List<Image>): List<Folder> {
        val folderMap: MutableMap<Long, Folder> = LinkedHashMap()
        for (image in images) {
            val bucketId = image.bucketId
            val bucketName = image.bucketName
            var folder = folderMap[bucketId]
            if (folder == null) {
                folder = Folder(bucketId, bucketName)
                folderMap[bucketId] = folder
            }
            folder.images.add(image)
        }
        return ArrayList(folderMap.values)
    }

    fun filter(images: ArrayList<Image>?, bucketId: Long?): ArrayList<Image> {
        if (images == null) return arrayListOf()

        if (bucketId == null || bucketId == 0L) return ArrayList(images)

        return images.filter { it.bucketId == bucketId } as ArrayList<Image>
    }

    fun filterNot(images: ArrayList<Image>?, bucketId: Long?): ArrayList<Image> {
        if (images == null) return arrayListOf()

        if (bucketId == null || bucketId == 0L) return ArrayList(images)

        return images.filter { it.bucketId != bucketId } as ArrayList<Image>
    }

    fun filterExclude(
        images: ArrayList<Image>?,
        excludedImages: ArrayList<Image>?
    ): ArrayList<Image> {
        if (images.isNullOrEmpty()) return arrayListOf()

        if (excludedImages.isNullOrEmpty()) return ArrayList(images)

        return images.filter { findImageIndex(it, excludedImages) == -1 } as ArrayList<Image>

    }

    fun findImageIndex(image: Image, images: ArrayList<Image>): Int {
        for (i in images.indices) {
            if (images[i].uri == image.uri) {
                return i
            }
        }

        return -1
    }

    fun findImageIndexes(subImages: ArrayList<Image>, images: ArrayList<Image>): ArrayList<Int> {
        val indexes = arrayListOf<Int>()
        for (image in subImages) {
            for (i in images.indices) {
                if (images[i].uri == image.uri) {
                    indexes.add(i)
                    break
                }
            }
        }

        return indexes
    }

    fun getBucketSelectionState(
        images: ArrayList<Image>?,
        selectedImages: ArrayList<Image>?,
        bucketId: Long?,
    ): SelectionState {
        if (bucketId == 0L) {
            if (images?.isEmpty() == true) return SelectionState.NOT_SELECTED
            return if (selectedImages?.isEmpty() == true) SelectionState.NOT_SELECTED else SelectionState.SELECTED
        } else {
            if (images?.any { it.bucketId == bucketId } == false) return SelectionState.NOT_SELECTED
            return if (selectedImages?.any { it.bucketId == bucketId } == false) SelectionState.NOT_SELECTED else SelectionState.SELECTED
        }
    }


    fun isGifFormat(image: Image): Boolean {
        val fileName = image.name
        val extension = if (fileName.contains(".")) {
            fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
        } else ""

        return extension.equals("gif", ignoreCase = true)
    }

    fun getImageCollectionUri(): Uri {
        return if (DeviceHelper.isMinSdk29) MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }
}