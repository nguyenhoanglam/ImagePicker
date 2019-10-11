package com.nguyenhoanglam.imagepicker.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File

/**
 * A Video that was picked by the user.
 */
@Parcelize
data class Video(override val id: Long, override val name: String, override val path: String): Asset, Parcelable {

    val thumbnailUri: Uri get() = Uri.fromFile(File(path))

}