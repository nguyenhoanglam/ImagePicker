/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.GridLayoutManager
import com.nguyenhoanglam.imagepicker.model.GridCount

object LayoutManagerHelper {

    fun newInstance(context: Context, gridCount: GridCount): CustomGridLayoutManager {
        val spanCount = getSpanCountForCurrentConfiguration(context, gridCount)
        return CustomGridLayoutManager(context, spanCount)
    }

    fun getSpanCountForCurrentConfiguration(context: Context, gridCount: GridCount): Int {
        val isPortrait =
            context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return if (isPortrait) gridCount.portrait else gridCount.landscape
    }
}

class CustomGridLayoutManager(context: Context?, spanCount: Int) :
    GridLayoutManager(context, spanCount) {

    private var isScrollEnabled = true

    fun setScrollEnabled(enabled: Boolean) {
        isScrollEnabled = enabled
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}