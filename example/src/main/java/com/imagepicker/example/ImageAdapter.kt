/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */
package com.imagepicker.example

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nguyenhoanglam.imagepicker.model.Image

internal class ImageAdapter(private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val images = ArrayList<Image>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val options = RequestOptions().placeholder(R.drawable.img_loading_placeholder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val (uri) = images[position]
        Glide.with(context)
            .load(uri)
            .apply(options)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)
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