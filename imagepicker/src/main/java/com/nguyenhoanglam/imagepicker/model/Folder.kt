/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import java.util.*

data class Folder(
    var bucketId: Long,
    var name: String,
    var images: ArrayList<Image> = arrayListOf()
)
