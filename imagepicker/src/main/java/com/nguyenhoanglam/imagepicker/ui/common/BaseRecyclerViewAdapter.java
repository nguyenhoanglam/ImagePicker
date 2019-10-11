package com.nguyenhoanglam.imagepicker.ui.common;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;

import com.nguyenhoanglam.imagepicker.ui.imagepicker.AssetLoader;

/**
 * Created by hoanglam on 8/17/17.
 */

public abstract class BaseRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private final Context context;
    private final LayoutInflater inflater;
    private final AssetLoader mAssetLoader;

    public BaseRecyclerViewAdapter(Context context, AssetLoader assetLoader) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mAssetLoader = assetLoader;
    }

    public AssetLoader getAssetLoader() {
        return mAssetLoader;
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }
}
