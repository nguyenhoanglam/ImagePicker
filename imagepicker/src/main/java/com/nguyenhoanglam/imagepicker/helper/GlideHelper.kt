/*
 * Copyright (C) 2023 Image Picker
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
        private var isTransitionEnabled = true

        private var options: RequestOptions =
            RequestOptions()
                .placeholder(R.drawable.imagepicker_img_placeholder)
                .error(R.drawable.imagepicker_img_placeholder)
                .centerCrop()

        fun setConfig(isTransitionEnabled: Boolean, placeholderResId: Int, errorResId: Int) {
            this.isTransitionEnabled = isTransitionEnabled
            options = options
                .placeholder(if (placeholderResId != 0) placeholderResId else R.drawable.imagepicker_img_placeholder)
                .error(if (errorResId != 0) errorResId else R.drawable.imagepicker_img_placeholder)
        }

        fun loadImage(imageView: ImageView, uri: Uri) {
            var builder = Glide.with(imageView.context)
                .load(uri)
                .apply(options)

            if (isTransitionEnabled) {
                builder = builder.transition(DrawableTransitionOptions.withCrossFade())
            }

            builder.into(imageView)
        }
    }
}