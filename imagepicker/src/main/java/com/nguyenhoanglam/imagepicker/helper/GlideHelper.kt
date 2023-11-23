/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.app.Activity
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
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

        fun loadPreviewImage(imageView: ShapeableImageView, uri: Uri) {
            imageView.post {
                Glide.with(imageView.context)
                    .load(uri)
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            var height =
                                resource.intrinsicHeight * imageView.measuredWidth / resource.intrinsicWidth

                            // Min height equals to image view width
                            if (height < imageView.measuredWidth) {
                                height = imageView.measuredWidth
                            }

                            // Max height equals to 80% of screen height
                            val maxHeight =
                                (DeviceHelper.getScreenHeight(imageView.context as Activity) * 0.8).toInt()

                            if (height > maxHeight) {
                                height = maxHeight
                            }

                            imageView.layoutParams.height = height
                            imageView.setImageDrawable(resource)

                            return false
                        }
                    })
                    .preload()
            }
        }
    }
}