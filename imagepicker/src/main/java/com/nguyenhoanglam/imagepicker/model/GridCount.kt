/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GridCount(val portrait: Int, val landscape: Int) : Parcelable