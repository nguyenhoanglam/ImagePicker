/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomDrawable(
    var backIcon: Int = 0,
    var cameraIcon: Int = 0,
    var selectAllIcon: Int = 0,
    var unselectAllIcon: Int = 0,
    var loadingImagePlaceholder: Int = 0,
    var errorImagePlaceholder: Int = 0
) : Parcelable
