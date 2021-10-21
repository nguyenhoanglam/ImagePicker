/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder?>(val context: Context) :
    RecyclerView.Adapter<T>() {
    val inflater: LayoutInflater = LayoutInflater.from(context)
}