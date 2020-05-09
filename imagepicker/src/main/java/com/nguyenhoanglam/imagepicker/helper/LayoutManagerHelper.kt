/*
 * Copyright (c) 2020 Nguyen Hoang Lam.
 * All rights reserved.
 */

package com.nguyenhoanglam.imagepicker.helper

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.GridLayoutManager

object LayoutManagerHelper {

    fun newInstance(context: Context, isFolder: Boolean = false): GridLayoutManager {
        val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val columns = if (isFolder) (if (isPortrait) 2 else 4) else (if (isPortrait) 3 else 5)
        return GridLayoutManager(context, columns)
    }

    fun getSpanCountForCurrentConfiguration(context: Context, isFolder: Boolean): Int {
        val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return if (isFolder) (if (isPortrait) 2 else 4) else (if (isPortrait) 3 else 5)
    }
}