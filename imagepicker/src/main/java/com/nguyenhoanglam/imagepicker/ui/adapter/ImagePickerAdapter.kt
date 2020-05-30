/*
 * Copyright (c) 2020 Nguyen Hoang Lam.
 * All rights reserved.
 */

package com.nguyenhoanglam.imagepicker.ui.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.helper.ImageHelper
import com.nguyenhoanglam.imagepicker.helper.ToastHelper
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.GlideLoader

class ImagePickerAdapter(context: Context, private val config: Config, private val imageSelectListener: OnImageSelectListener) : BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder?>(context) {

    private val glideLoader = GlideLoader()
    private val selectedImages = arrayListOf<Image>()
    private val images: ArrayList<Image> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = inflater.inflate(R.layout.imagepicker_item_image, parent, false)
        return ImageViewHolder(itemView, config.isShowNumberIndicator, config.getIndicatorColor())
    }

    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, position)
        } else {
            if (payloads.any { it is ImageUnselected }) {
                val image = images[position]
                val selectedIndex = ImageHelper.findImageIndex(image, selectedImages)
                viewHolder.selectedNumber.text = (selectedIndex + 1).toString()
            }
        }
    }

    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int) {
        val image = images[position]
        val selectedIndex = ImageHelper.findImageIndex(image, selectedImages)
        val isSelected = config.isMultipleMode && selectedIndex != -1

        glideLoader.loadImage(image.id, image.path, viewHolder.image)

        viewHolder.gifIndicator.visibility = if (ImageHelper.isGifFormat(image)) View.VISIBLE else View.GONE
        viewHolder.selectedIcon.visibility = if (isSelected && !config.isShowNumberIndicator) View.VISIBLE else View.GONE
        viewHolder.selectedNumber.visibility = if (isSelected && config.isShowNumberIndicator) View.VISIBLE else View.GONE
        if (viewHolder.selectedNumber.visibility == View.VISIBLE) {
            viewHolder.selectedNumber.text = (selectedIndex + 1).toString()
        }
        viewHolder.itemView.setOnClickListener {
            selectOrRemoveImage(image, position)
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun selectOrRemoveImage(image: Image, position: Int) {
        if (config.isMultipleMode) {
            val selectedIndex = ImageHelper.findImageIndex(image, selectedImages)
            if (selectedIndex != -1) {
                selectedImages.removeAt(selectedIndex)
                notifyItemChanged(position)
                if (config.isShowNumberIndicator) {
                    val indexes = ImageHelper.findImageIndexes(selectedImages, images)
                    for (index in indexes) {
                        notifyItemChanged(index, ImageUnselected())
                    }
                }
            } else {
                if (selectedImages.size >= config.maxSize) {
                    val message = if (config.limitMessage != null) config.limitMessage!! else String.format(context.resources.getString(R.string.imagepicker_msg_limit_images), config.maxSize)
                    ToastHelper.show(context, message)
                    return
                } else {
                    selectedImages.add(image)
                    notifyItemChanged(position)
                }
            }
            imageSelectListener.onSelectedImagesChanged(selectedImages)
        } else {
            imageSelectListener.onSingleModeImageSelected(image)
        }
    }

    fun setData(images: List<Image>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }

    fun setSelectedImages(selectedImages: ArrayList<Image>) {
        this.selectedImages.clear()
        this.selectedImages.addAll(selectedImages)
        notifyDataSetChanged()

    }

    class ImageViewHolder(itemView: View, isShowNumberIndicator: Boolean, indicatorColor: Int) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_thumbnail)
        val selectedIcon: ImageView = itemView.findViewById(R.id.image_selected_icon)
        val selectedNumber: TextView = itemView.findViewById(R.id.text_selected_number)
        val gifIndicator: View = itemView.findViewById(R.id.gif_indicator)

        init {
            val drawable: GradientDrawable = (if (isShowNumberIndicator) selectedNumber.background.mutate() else selectedIcon.background.mutate()) as GradientDrawable
            drawable.setColor(indicatorColor)
        }
    }

    class ImageUnselected
}