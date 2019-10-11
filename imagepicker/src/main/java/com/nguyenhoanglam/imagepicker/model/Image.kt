package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * An Image that was picked by the user.
 */
@Parcelize
data class Image(override val id: Long, override val name: String, override val path: String): Asset, Parcelable