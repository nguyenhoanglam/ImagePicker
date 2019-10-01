package com.nguyenhoanglam.imagepicker.ui.imagepicker;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.Video;


/**
 * Created by hoanglam on 8/17/17.
 */

public class AssetLoader {

    private RequestOptions options;

    public AssetLoader() {
        options = new RequestOptions()
                .placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    public void loadAsset(Asset asset, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(asset instanceof Image ? asset.getPath() : ((Video)asset).getThumbnail())
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}
