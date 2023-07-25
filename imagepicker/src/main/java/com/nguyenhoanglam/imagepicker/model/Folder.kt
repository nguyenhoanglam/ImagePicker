/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

data class Folder(
    var bucketId: Long,
    var name: String,
    var images: ArrayList<Image> = arrayListOf()
)
