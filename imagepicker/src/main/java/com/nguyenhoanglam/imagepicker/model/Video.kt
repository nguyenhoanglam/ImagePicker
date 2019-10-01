package com.nguyenhoanglam.imagepicker.model

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * A Video that was picked by the user.
 */
@Parcelize
data class Video(override val id: Long, override val name: String, override val path: String): Asset, Parcelable {

    @IgnoredOnParcel
    val thumbnail: Bitmap by lazy { ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND) }

}