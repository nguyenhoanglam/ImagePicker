/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.helper.GlideHelper
import com.nguyenhoanglam.imagepicker.helper.ImageHelper
import com.nguyenhoanglam.imagepicker.helper.ToastHelper
import com.nguyenhoanglam.imagepicker.listener.OnImageLongPressListener
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectListener
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.IndicatorType

class ImagePickerAdapter(
    context: Context,
    private val config: ImagePickerConfig,
    private val imageSelectListener: OnImageSelectListener,
    private val imageLongPressListener: OnImageLongPressListener
) : BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder?>(context) {

    private var images = arrayListOf<Image>()
    private var selectedImages = arrayListOf<Image>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = inflater.inflate(R.layout.imagepicker_item_image, parent, false)
        return ImageViewHolder(
            itemView, config.selectedIndicatorType, config.customColor!!.selectedImageIndicator!!
        )
    }

    override fun onBindViewHolder(
        viewHolder: ImageViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, position)
        } else {
            when {
                payloads.any { it is ImageSelectedOrUpdated } -> {
                    when (config.selectedIndicatorType) {
                        IndicatorType.NUMBER -> {
                            val image = images[position]
                            val selectedIndex = ImageHelper.findImageIndex(image, selectedImages)
                            viewHolder.selectedNumber.text = "${selectedIndex + 1}"
                            viewHolder.selectedNumber.visibility = View.VISIBLE
                            viewHolder.selectedIcon.visibility = View.GONE
                        }

                        else -> {
                            viewHolder.selectedIcon.visibility = View.VISIBLE
                            viewHolder.selectedNumber.visibility = View.GONE
                        }
                    }

                    setupItemForeground(viewHolder.image, true)
                }

                payloads.any { it is ImageUnselected } -> {
                    when (config.selectedIndicatorType) {
                        IndicatorType.NUMBER -> viewHolder.selectedNumber.visibility = View.GONE
                        else -> viewHolder.selectedIcon.visibility = View.GONE
                    }

                    setupItemForeground(viewHolder.image, false)
                }

                else -> {
                    onBindViewHolder(viewHolder, position)
                }
            }
        }
    }

    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int) {
        val image = images[position]
        val selectedIndex = ImageHelper.findImageIndex(image, selectedImages)
        val isSelected = !config.isSingleSelectMode && selectedIndex != -1

        GlideHelper.loadImage(viewHolder.image, image.uri)
        setupItemForeground(viewHolder.image, isSelected)

        viewHolder.gifIndicator.visibility =
            if (ImageHelper.isGifFormat(image)) View.VISIBLE else View.GONE
        viewHolder.selectedNumber.visibility =
            if (isSelected && config.selectedIndicatorType == IndicatorType.NUMBER) View.VISIBLE else View.GONE
        viewHolder.selectedIcon.visibility =
            if (isSelected && config.selectedIndicatorType == IndicatorType.CHECK_MARK) View.VISIBLE else View.GONE
        if (viewHolder.selectedNumber.visibility == View.VISIBLE) {
            viewHolder.selectedNumber.text = "${selectedIndex + 1}"
        }
        viewHolder.itemView.setOnClickListener {
            selectOrRemoveImage(image, position)
        }
        viewHolder.itemView.setOnLongClickListener {
            imageLongPressListener.onLongPress(image)
            true
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    private fun setupItemForeground(view: View, isSelected: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.foreground = if (isSelected) ColorDrawable(
                ContextCompat.getColor(
                    context, R.color.imagepicker_black_alpha_30
                )
            ) else null
        }
    }

    private fun selectOrRemoveImage(image: Image, position: Int) {
        if (config.isSingleSelectMode) {
            imageSelectListener.onSingleModeImageSelected(image)
        } else {
            val selectedIndex = ImageHelper.findImageIndex(image, selectedImages)
            if (selectedIndex != -1) {
                selectedImages.removeAt(selectedIndex)
                notifyItemChanged(position, ImageUnselected())
                val indexes = ImageHelper.findImageIndexes(selectedImages, images)
                for (index in indexes) {
                    notifyItemChanged(index, ImageSelectedOrUpdated())
                }
            } else {
                if (selectedImages.size >= config.limitSize) {
                    ToastHelper.show(context, config.customMessage!!.reachLimitSize!!)
                    return
                } else {
                    selectedImages.add(image)
                    notifyItemChanged(position, ImageSelectedOrUpdated())
                }
            }
            imageSelectListener.onSelectedImagesChanged(selectedImages)
        }
    }

    fun setImages(images: ArrayList<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    fun setSelectedImages(selectedImages: ArrayList<Image>) {
        this.selectedImages = selectedImages
        notifyDataSetChanged()
    }

    fun setSelectedAndAddedImages(selectedImages: ArrayList<Image>, addedImages: ArrayList<Image>) {
        this.selectedImages = selectedImages
        val addedIndexes = ImageHelper.findImageIndexes(addedImages, images)
        for (index in addedIndexes) {
            notifyItemChanged(index, ImageSelectedOrUpdated())
        }
    }

    fun setSelectedAndRemovedImages(
        selectedImages: ArrayList<Image>,
        removedImages: ArrayList<Image>
    ) {
        this.selectedImages = selectedImages
        val removedIndexes = ImageHelper.findImageIndexes(removedImages, images)
        for (index in removedIndexes) {
            notifyItemChanged(index, ImageUnselected())
        }
    }

    class ImageViewHolder(itemView: View, indicatorType: IndicatorType, indicatorColor: String) :
        RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_thumbnail)
        val selectedIcon: ImageView = itemView.findViewById(R.id.image_selected_icon)
        val selectedNumber: TextView = itemView.findViewById(R.id.text_selected_number)
        val gifIndicator: View = itemView.findViewById(R.id.gif_indicator)

        init {
            val drawable: GradientDrawable = when (indicatorType) {
                IndicatorType.NUMBER -> selectedNumber.background.mutate()
                else -> selectedIcon.background.mutate()
            } as GradientDrawable

            drawable.setColor(Color.parseColor(indicatorColor))
        }
    }

    class ImageSelectedOrUpdated

    class ImageUnselected
}