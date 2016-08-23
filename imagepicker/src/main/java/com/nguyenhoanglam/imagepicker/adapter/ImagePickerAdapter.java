package com.nguyenhoanglam.imagepicker.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoanglam on 7/31/16.
 */
public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder> {

    private Context context;
    private List<Image> images;
    private List<Image> selectedImages;
    private ViewHolder.OnItemClickListener onItemClickListener;
    private LayoutInflater inflater;
    private int size;
    private int imageSize;
    private int padding;

    public ImagePickerAdapter(Context context, List<Image> images, List<Image> selectedImages, ViewHolder.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.images = images;
        this.selectedImages = selectedImages;
        this.onItemClickListener = onItemClickListener;
        inflater = LayoutInflater.from(this.context);
        padding = context.getResources().getDimensionPixelSize(R.dimen.item_padding);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(itemView, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Image image = images.get(position);

        viewHolder.imageView.getLayoutParams().width = imageSize;
        viewHolder.imageView.getLayoutParams().height = imageSize;

        viewHolder.itemView.getLayoutParams().width = size;
        viewHolder.itemView.getLayoutParams().height = size;

        Glide.with(context)
                .load(image.getPath())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .override(imageSize, imageSize)
                .centerCrop()
                .into(viewHolder.imageView);

        if (isSelected(image)) {
            viewHolder.view.setAlpha(0.5f);
            ((FrameLayout) viewHolder.itemView).setForeground(ContextCompat.getDrawable(context,R.drawable.ic_done_white));
        } else {
            viewHolder.view.setAlpha(0.0f);
            ((FrameLayout) viewHolder.itemView).setForeground(null);
        }

    }

    private boolean isSelected(Image image) {
        for (Image selectedImage:selectedImages) {
            if (selectedImage.getPath().equals(image.getPath())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public void setImageSize(int size) {
        this.size = size;
        imageSize = size - padding * 2;
    }

    public void clear() {
        images.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Image> images) {
        int startIndex = this.images.size();
        this.images.addAll(startIndex, images);
        notifyItemRangeInserted(startIndex, images.size());
    }

    public void addSelected(Image image) {
        selectedImages.add(image);
        notifyItemChanged(images.indexOf(image));
    }

    public void removeSelected(Image image) {
        selectedImages.remove(image);
        notifyItemChanged(images.indexOf(image));
//        for (int i = 0; i < selectedImages.size(); i++) {
//            Image image1 = selectedImages.get(i);
//            notifyItemChanged(images.indexOf(image1));
//        }
    }

    public void removeSelectedPosition(int position, int clickPosition) {
        selectedImages.remove(position);
        notifyItemChanged(clickPosition);
    }

    public void removeAllSelectedSingleClick() {
        selectedImages.clear();
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private View view;
        private final OnItemClickListener onItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            view = itemView.findViewById(R.id.view_alpha);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            view.setSelected(true);
            onItemClickListener.onClick(view, getAdapterPosition());
        }

        public interface OnItemClickListener {
            void onClick(View view, int position);
        }
    }


}
