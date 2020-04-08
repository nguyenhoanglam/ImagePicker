package com.nguyenhoanglam.imagepicker.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.helper.ImageHelper
import com.nguyenhoanglam.imagepicker.listener.OnImageClickListener
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectionListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.common.BaseRecyclerViewAdapter
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImageLoader
import java.util.*

/**
 * Created by hoanglam on 7/31/16.
 */
class ImagePickerAdapter(context: Context, private val config: Config, imageLoader: ImageLoader, selectedImages: List<Image>, private val itemClickListener: OnImageClickListener) : BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder?>(context, imageLoader) {

    private val images: MutableList<Image> = ArrayList()
    private val selectedImages: MutableList<Image> = ArrayList()
    private var imageSelectionListener: OnImageSelectionListener? = null

    init {
        if (selectedImages != null && selectedImages.isNotEmpty()) {
            this.selectedImages.addAll(selectedImages)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = inflater.inflate(R.layout.imagepicker_item_image, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ImageViewHolder, position: Int) {
        val image = images[position]
        val selectedPosition = getSelectedPosition(image)
        val isSelected = selectedPosition != -1

        imageLoader.loadImage(image.path, viewHolder.image)
        viewHolder.gifIndicator.visibility = if (ImageHelper.isGifFormat(image)) View.VISIBLE else View.GONE
        viewHolder.selectedIcon.visibility = if (isSelected && !config.isShowSelectedAsNumber) View.VISIBLE else View.GONE
        viewHolder.selectedNumber.visibility = if (isSelected && config.isShowSelectedAsNumber) View.VISIBLE else View.GONE
        if (viewHolder.selectedNumber.visibility == View.VISIBLE) {
            viewHolder.selectedNumber.text = (selectedPosition + 1).toString()
        }
        viewHolder.itemView.setOnClickListener { view ->
            val shouldSelect = itemClickListener.onImageClick(view, viewHolder.adapterPosition, !isSelected)
            when {
                isSelected -> {
                    removeSelected(image, position)
                }
                shouldSelect -> {
                    addSelected(image, position)
                }
                else -> {
                    val message = String.format(config.limitMessage, config.maxSize)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getSelectedPosition(image: Image): Int {
        for (i in selectedImages.indices) {
            val selectedImage = selectedImages[i]
            if (selectedImage.path == image.path) {
                return i
            }
        }
        return -1
    }

    fun setOnImageSelectionListener(imageSelectedListener: OnImageSelectionListener?) {
        imageSelectionListener = imageSelectedListener
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setData(images: List<Image>?) {
        if (images != null) {
            this.images.clear()
            this.images.addAll(images)
        }
        notifyDataSetChanged()
    }

    fun addSelected(images: List<Image>?) {
        selectedImages.addAll(images!!)
        notifySelectionChanged()
    }

    private fun addSelected(image: Image, position: Int) {
        selectedImages.add(image)
        notifyItemChanged(position)
        notifySelectionChanged()
    }

    private fun removeSelected(image: Image, position: Int) {
        val itr = selectedImages.iterator()
        while (itr.hasNext()) {
            val itrImage = itr.next()
            if (itrImage.id == image.id) {
                itr.remove()
                break
            }
        }
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    fun removeAllSelected() {
        selectedImages.clear()
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    private fun notifySelectionChanged() {
        if (imageSelectionListener != null) {
            imageSelectionListener!!.onSelectionUpdate(selectedImages)
        }
    }

    fun getSelectedImages(): List<Image> {
        return selectedImages
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.image_thumbnail)
        val selectedIcon: ImageView = itemView.findViewById(R.id.image_selected_icon)
        val selectedNumber: TextView = itemView.findViewById(R.id.text_selected_number)
        val gifIndicator: View = itemView.findViewById(R.id.gif_indicator)

    }
}