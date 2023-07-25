/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig

class ImagePickerToolbar : RelativeLayout {

    private var config: ImagePickerConfig? = null
    private lateinit var titleText: TextView
    private lateinit var doneText: TextView
    private lateinit var backImage: AppCompatImageView
    private lateinit var cameraImage: AppCompatImageView
    private lateinit var selectAllImage: AppCompatImageView
    private lateinit var unselectAllImage: AppCompatImageView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.imagepicker_toolbar, this)
        if (isInEditMode) {
            return
        }

        titleText = findViewById(R.id.text_toolbar_title)
        doneText = findViewById(R.id.text_toolbar_done)
        backImage = findViewById(R.id.image_toolbar_back)
        cameraImage = findViewById(R.id.image_toolbar_camera)
        selectAllImage = findViewById(R.id.image_toolbar_select_all)
        unselectAllImage = findViewById(R.id.image_toolbar_unselect_all)
    }

    fun setConfig(config: ImagePickerConfig) {
        this.config = config

        setBackgroundColor(Color.parseColor(config.customColor!!.toolbar))

        titleText.apply {
            text = if (config.isFolderMode) config.folderTitle else config.imageTitle
            setTextColor(Color.parseColor(config.customColor!!.toolbarTitle))
        }

        doneText.apply {
            text = config.doneButtonTitle
            setTextColor(Color.parseColor(config.customColor!!.doneButtonTitle))
            visibility = if (config.isAlwaysShowDoneButton) View.VISIBLE else View.GONE
        }

        backImage.setResourceAndColor(
            config.customDrawable!!.backIcon,
            R.drawable.imagepicker_ic_back,
            config.customColor!!.toolbarIcon!!
        )

        cameraImage.apply {
            setResourceAndColor(
                config.customDrawable!!.cameraIcon,
                R.drawable.imagepicker_ic_camera,
                config.customColor!!.toolbarIcon!!
            )
            visibility = if (config.isShowCamera) View.VISIBLE else View.GONE
        }

        selectAllImage.apply {
            setResourceAndColor(
                config.customDrawable!!.selectAllIcon,
                R.drawable.imagepicker_ic_select_all,
                config.customColor!!.toolbarIcon!!
            )
            visibility = View.GONE
        }

        unselectAllImage.apply {
            setResourceAndColor(
                config.customDrawable!!.unselectAllIcon,
                R.drawable.imagepicker_ic_unselect_all,
                config.customColor!!.toolbarIcon!!
            )
            visibility = View.GONE
        }
    }

    fun setTitle(title: String?) {
        titleText.text = title
    }

    fun showDoneButton(isShow: Boolean) {
        doneText.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun showSelectAllButton() {
        selectAllImage.visibility = View.VISIBLE
    }

    fun hideSelectAllButton() {
        selectAllImage.visibility = View.GONE
    }

    fun showUnselectAllButton() {
        unselectAllImage.visibility = View.VISIBLE
    }

    fun hideUnselectAllButton() {
        unselectAllImage.visibility = View.GONE
    }

    fun hideSelectButtons() {
        selectAllImage.visibility = View.GONE
        unselectAllImage.visibility = View.GONE
    }

    fun setOnBackClickListener(clickListener: OnClickListener) {
        backImage.setOnClickListener(clickListener)
    }

    fun setOnSelectAllClickListener(clickListener: OnClickListener) {
        selectAllImage.setOnClickListener(clickListener)
    }

    fun setOnUnselectAllClickListener(clickListener: OnClickListener) {
        unselectAllImage.setOnClickListener(clickListener)
    }

    fun setOnCameraClickListener(clickListener: OnClickListener) {
        cameraImage.setOnClickListener(clickListener)
    }

    fun setOnDoneClickListener(clickListener: OnClickListener) {
        doneText.setOnClickListener(clickListener)
    }
}

fun AppCompatImageView.setResourceAndColor(iconResId: Int, defaultIconResId: Int, color: String) {
    this.setImageResource(if (iconResId == 0) defaultIconResId else iconResId)
    if (iconResId != 0) this.clearColorFilter()
    else this.setColorFilter(Color.parseColor(color))

}