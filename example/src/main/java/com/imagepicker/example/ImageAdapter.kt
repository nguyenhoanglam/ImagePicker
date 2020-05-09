/*
 * Copyright (c) 2020 Nguyen Hoang Lam.
 * All rights reserved.
 */
package com.imagepicker.example

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nguyenhoanglam.imagepicker.model.Image

internal class ImageAdapter(private val context: Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val images = ArrayList<Image>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val options = RequestOptions().placeholder(R.drawable.image_placeholder)
        .error(R.drawable.image_placeholder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (id, _, path) = images[position]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            Glide.with(context)
                .load(uri)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView)
        } else {
            Glide.with(context)
                .load(path)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setData(images: List<Image>?) {
        this.images.clear()
        if (images != null) {
            this.images.addAll(images)
        }
        notifyDataSetChanged()
    }

    internal class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_thumbnail)

    }
}