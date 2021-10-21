/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nguyenhoanglam.imagepicker.R

class GlideHelper {

    companion object {
        private val options: RequestOptions =
            RequestOptions().placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_error)
                .centerCrop()

        fun loadImage(imageView: ImageView, uri: Uri) {
            Glide.with(imageView.context)
                .load(uri)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)

        }
    }
}