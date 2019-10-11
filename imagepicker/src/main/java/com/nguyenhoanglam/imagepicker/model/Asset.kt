package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable

/**
 * Represents an Asset that can be picked by the user, typically this will be either an Image or a Video.
 */
interface Asset: Parcelable {
    val id: Long
    val name: String
    val path: String
}