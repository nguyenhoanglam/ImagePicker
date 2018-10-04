package com.nguyenhoanglam.imagepicker.adapter;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.helper.ImageHelper;
import com.nguyenhoanglam.imagepicker.listener.OnImageClickListener;
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectionListener;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.common.BaseRecyclerViewAdapter;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoanglam on 7/31/16.
 */
public class ImagePickerAdapter extends BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder> {

    private List<Image> images = new ArrayList<>();
    private List<Image> selectedImages = new ArrayList<>();
    private OnImageClickListener itemClickListener;
    private OnImageSelectionListener imageSelectionListener;

    public ImagePickerAdapter(Context context, ImageLoader imageLoader, List<Image> selectedImages, OnImageClickListener itemClickListener) {
        super(context, imageLoader);
        this.itemClickListener = itemClickListener;

        if (selectedImages != null && !selectedImages.isEmpty()) {
            this.selectedImages.addAll(selectedImages);
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = getInflater().inflate(R.layout.imagepicker_item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder viewHolder, final int position) {

        final Image image = images.get(position);
        final boolean isSelected = isSelected(image);

        getImageLoader().loadImage(image.getPath(), viewHolder.image);

        viewHolder.gifIndicator.setVisibility(ImageHelper.isGifFormat(image) ? View.VISIBLE : View.GONE);
        viewHolder.alphaView.setAlpha(isSelected ? 0.5f : 0.0f);
        viewHolder.container.setForeground(isSelected
                ? ContextCompat.getDrawable(getContext(), R.drawable.imagepicker_ic_selected)
                : null);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean shouldSelect = itemClickListener.onImageClick(view, viewHolder.getAdapterPosition(), !isSelected);
                if (isSelected) {
                    removeSelected(image, position);
                } else if (shouldSelect) {
                    addSelected(image, position);
                }
            }
        });
    }

    private boolean isSelected(Image image) {
        for (Image selectedImage : selectedImages) {
            if (selectedImage.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    public void setOnImageSelectionListener(OnImageSelectionListener imageSelectedListener) {
        this.imageSelectionListener = imageSelectedListener;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public void setData(List<Image> images) {
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
        notifyDataSetChanged();
    }

    public void addSelected(List<Image> images) {
        selectedImages.addAll(images);
        notifySelectionChanged();
    }

    public void addSelected(Image image, int position) {
        selectedImages.add(image);
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeSelected(Image image, int position) {
        selectedImages.remove(image);
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeAllSelected() {
        selectedImages.clear();
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        if (imageSelectionListener != null) {
            imageSelectionListener.onSelectionUpdate(selectedImages);
        }
    }

    public List<Image> getSelectedImages() {
        return selectedImages;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout container;
        private ImageView image;
        private View alphaView;
        private View gifIndicator;

        public ImageViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
            image = itemView.findViewById(R.id.image_thumbnail);
            alphaView = itemView.findViewById(R.id.view_alpha);
            gifIndicator = itemView.findViewById(R.id.gif_indicator);

        }

    }

}
