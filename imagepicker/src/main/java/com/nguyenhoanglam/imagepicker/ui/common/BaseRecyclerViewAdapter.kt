package com.nguyenhoanglam.imagepicker.ui.common

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImageLoader

/**
 * Created by hoanglam on 8/17/17.
 */
abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder?>(val context: Context, imageLoader: ImageLoader) : RecyclerView.Adapter<T>() {
    val inflater: LayoutInflater = LayoutInflater.from(context)
    val imageLoader: ImageLoader = imageLoader

}