package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * A Video that was picked by the user.
 */
@Parcelize
data class Video(override val id: Long, override val name: String, override val path: String): Asset, Parcelable {

    val thumbnailPath: String get() {
        // Use the video path to get a thumbnail for the video.
        return "https://picsum.photos/200"
    }

}