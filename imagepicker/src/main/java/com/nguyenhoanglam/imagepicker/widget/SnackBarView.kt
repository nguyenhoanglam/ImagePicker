/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.Interpolator
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig

class SnackBarView : RelativeLayout {

    private lateinit var messageText: TextView
    private lateinit var actionButton: Button

    private var isShowing = false

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        View.inflate(context, R.layout.imagepicker_snackbar, this)
        if (isInEditMode) {
            return
        }

        translationY = height.toFloat()
        alpha = 0f
        isShowing = false
        val horizontalPadding = convertDpToPixels(context, 24f)
        val verticalPadding = convertDpToPixels(context, 14f)
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
        messageText = findViewById(R.id.text_snackbar_message)
        actionButton = findViewById(R.id.button_snack_bar_action)
    }

    fun config(config: ImagePickerConfig) {
        setBackgroundColor(Color.parseColor(config.customColor!!.snackBarBackground))
        messageText.setTextColor(Color.parseColor(config.customColor!!.snackBarMessage))
        actionButton.text = config.snackBarButtonTitle
        actionButton.setTextColor(Color.parseColor(config.customColor!!.snackBarButtonTitle))
    }

    private fun setOnActionClickListener(onClickListener: OnClickListener) {
        actionButton.setOnClickListener { view -> hide { onClickListener.onClick(view) } }
    }

    fun show(message: String?, onClickListener: OnClickListener) {
        messageText.text = message ?: ""
        setOnActionClickListener(onClickListener)
        ViewCompat.animate(this)
            .translationY(0f)
            .setDuration(ANIM_DURATION.toLong())
            .setInterpolator(INTERPOLATOR)
            .alpha(1f)
        isShowing = true
    }

    private fun hide(runnable: Runnable) {
        ViewCompat.animate(this)
            .translationY(height.toFloat())
            .setDuration(ANIM_DURATION.toLong())
            .alpha(0.5f)
            .withEndAction(runnable)
        isShowing = false
    }

    private fun convertDpToPixels(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
            .toInt()
    }

    companion object {
        private const val ANIM_DURATION = 200
        private val INTERPOLATOR: Interpolator = FastOutLinearInInterpolator()
    }
}