package com.nguyenhoanglam.imagepicker.ui.imagepicker;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.nguyenhoanglam.imagepicker.R;


/**
 * Created by hoanglam on 8/17/17.
 */

public class ImageLoader {

    private RequestOptions options;

    public ImageLoader() {
        options = new RequestOptions()
                .placeholder(R.drawable.imagepicker_image_placeholder)
                .error(R.drawable.imagepicker_image_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
    }

    public void loadImage(String path, ImageView imageView) {
        Glide.with(imageView.getContext())
                .load(path)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}
