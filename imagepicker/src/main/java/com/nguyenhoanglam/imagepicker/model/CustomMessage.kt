/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomMessage(
    var reachLimitSize: String? = null,
    var cameraError: String? = null,
    var noCamera: String? = null,
    var noImage: String? = null,
    var noPhotoAccessPermission: String? = null,
    var noCameraPermission: String? = null,
) : Parcelable
