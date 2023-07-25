/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var uri: Uri,
    var name: String,
    var bucketId: Long = 0,
    var bucketName: String = "",
    var addedDate: Long = 0
) : Parcelable