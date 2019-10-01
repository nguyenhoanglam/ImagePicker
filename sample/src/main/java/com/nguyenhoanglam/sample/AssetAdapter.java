package com.nguyenhoanglam.sample;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoanglam on 8/23/17.
 */

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ImageViewHolder> {

    private Context context;
    private List<Asset> assets;
    private LayoutInflater inflater;

    public AssetAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        assets = new ArrayList<>();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(inflater.inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        final Asset asset = assets.get(position);
        Glide.with(context)
                .load(asset instanceof Image ? asset.getPath(): ((Video)asset).getThumbnailUri())
                .override(holder.imageView.getWidth(), holder.imageView.getHeight())
                .apply(new RequestOptions().placeholder(R.drawable.image_placeholder).error(R.drawable.image_placeholder))
                .into(holder.imageView);

        holder.videoIndicator.setVisibility(asset instanceof Video ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return assets.size();
    }

    public void setData(List<Asset> assets) {
        this.assets.clear();
        if (assets != null) {
            this.assets.addAll(assets);
        }
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ImageView videoIndicator;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_thumbnail);
            videoIndicator = itemView.findViewById(R.id.image_video_icon);
        }
    }
}
