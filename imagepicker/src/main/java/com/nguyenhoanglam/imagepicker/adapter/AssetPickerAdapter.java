package com.nguyenhoanglam.imagepicker.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.helper.ImageHelper;
import com.nguyenhoanglam.imagepicker.listener.OnAssetClickListener;
import com.nguyenhoanglam.imagepicker.listener.OnAssetSelectionListener;
import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.Video;
import com.nguyenhoanglam.imagepicker.ui.common.BaseRecyclerViewAdapter;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.AssetLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hoanglam on 7/31/16.
 */
public class AssetPickerAdapter extends BaseRecyclerViewAdapter<AssetPickerAdapter.ImageViewHolder> {

    private List<Asset> assets = new ArrayList<>();
    private List<Asset> selectedAssets = new ArrayList<>();
    private OnAssetClickListener itemClickListener;
    private OnAssetSelectionListener assetSelectionListener;

    public AssetPickerAdapter(Context context, AssetLoader assetLoader, List<Asset> selectedAssets, OnAssetClickListener itemClickListener) {
        super(context, assetLoader);
        this.itemClickListener = itemClickListener;

        if (selectedAssets != null && !selectedAssets.isEmpty()) {
            this.selectedAssets.addAll(selectedAssets);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = getInflater().inflate(R.layout.imagepicker_item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder viewHolder, final int position) {

        final Asset asset = assets.get(position);
        final boolean isSelected = isSelected(asset);

        getAssetLoader().loadAsset(asset, viewHolder.image);

        if (asset instanceof Image) {
            viewHolder.gifIndicator.setVisibility(ImageHelper.isGifFormat((Image)asset) ? View.VISIBLE : View.GONE);
        } else {
            viewHolder.gifIndicator.setVisibility(View.GONE);
            // If video, show video indicator
        }
        viewHolder.videoIndicator.setVisibility(asset instanceof Video ? View.VISIBLE : View.GONE);
        viewHolder.alphaView.setAlpha(isSelected ? 0.5f : 0.0f);
        viewHolder.container.setForeground(isSelected
                ? ContextCompat.getDrawable(getContext(), R.drawable.imagepicker_ic_selected)
                : null);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean shouldSelect = itemClickListener.onAssetClick(view, viewHolder.getAdapterPosition(), !isSelected);
                if (isSelected) {
                    removeSelected(asset, position);
                } else if (shouldSelect) {
                    addSelected(asset, position);
                }
            }
        });
    }

    private boolean isSelected(Asset asset) {
        for (Asset selectedImage : selectedAssets) {
            if (selectedImage.getPath().equals(asset.getPath())) {
                return true;
            }
        }
        return false;
    }

    public void setOnImageSelectionListener(OnAssetSelectionListener assetSelectedListener) {
        this.assetSelectionListener = assetSelectedListener;
    }

    @Override
    public int getItemCount() {
        return assets.size();
    }


    public void setData(List<Asset> assets) {
        if (assets != null) {
            this.assets.clear();
            this.assets.addAll(assets);
        }
        notifyDataSetChanged();
    }

    public void addSelected(List<Asset> assets) {
        selectedAssets.addAll(assets);
        notifySelectionChanged();
    }

    public void addSelected(Asset asset, int position) {
        selectedAssets.add(asset);
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeSelected(Asset asset, int position) {
        Iterator<Asset> itr = selectedAssets.iterator();
        while (itr.hasNext()) {
            Asset itrImage = itr.next();
            if (itrImage.getId() == asset.getId()) {
                itr.remove();
                break;
            }
        }
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeAllSelected() {
        selectedAssets.clear();
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        if (assetSelectionListener != null) {
            assetSelectionListener.onSelectionUpdate(selectedAssets);
        }
    }

    public List<Asset> getSelectedAssets() {
        return selectedAssets;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout container;
        private ImageView image;
        private View alphaView;
        private View gifIndicator;
        private ImageView videoIndicator;

        public ImageViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
            image = itemView.findViewById(R.id.image_thumbnail);
            alphaView = itemView.findViewById(R.id.view_alpha);
            gifIndicator = itemView.findViewById(R.id.gif_indicator);
            videoIndicator = itemView.findViewById(R.id.image_video_icon);
        }

    }

}
