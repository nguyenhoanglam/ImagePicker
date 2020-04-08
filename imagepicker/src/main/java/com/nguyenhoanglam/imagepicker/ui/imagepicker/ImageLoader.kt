package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nguyenhoanglam.imagepicker.R

/**
 * Created by hoanglam on 8/17/17.
 */
class ImageLoader {

    private val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.imagepicker_image_placeholder)
            .error(R.drawable.imagepicker_image_placeholder)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    fun loadImage(path: String?, imageView: ImageView) {
        Glide.with(imageView.context)
                .load(path)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

}