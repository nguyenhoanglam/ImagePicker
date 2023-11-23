/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class SortBy {
    DATE_ADDED,
    DATE_MODIFIED,
    DISPLAY_NAME
}

enum class SortOrder {
    ASC,
    DESC,
}

@Parcelize
data class ImageSort(val by: SortBy, val order: SortOrder) : Parcelable