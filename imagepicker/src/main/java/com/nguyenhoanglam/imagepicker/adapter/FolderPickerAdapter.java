package com.nguyenhoanglam.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.listeners.OnFolderClickListener;
import com.nguyenhoanglam.imagepicker.model.Folder;

import java.util.List;

/**
 * Created by boss1088 on 8/22/16.
 */
public class FolderPickerAdapter extends RecyclerView.Adapter<FolderPickerAdapter.FoldersViewHolder> {

    private int size;
    private int imageSize;
    private int padding;
    private final OnFolderClickListener folderClickListener;

    private List<Folder> folders;

    public FolderPickerAdapter(Context context, OnFolderClickListener folderClickListener) {
        this.folderClickListener = folderClickListener;
        padding = context.getResources().getDimensionPixelSize(R.dimen.item_padding);
    }

    @Override
    public FoldersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FoldersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, null));
    }

    @Override
    public void onBindViewHolder(final FoldersViewHolder holder, int position) {
        holder.image.getLayoutParams().width = imageSize;
        holder.image.getLayoutParams().height = imageSize;

        holder.image.getLayoutParams().width = size;
        holder.image.getLayoutParams().height = size;

        Glide.with(holder.image.getContext())
                .load(folders.get(position).getImages().get(0).getPath()) // Uri of the picture
                .override(imageSize, imageSize)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .centerCrop()
                .into(holder.image);

        holder.name.setText(folders.get(position).getFolderName());
        holder.files.setText(String.valueOf(folders.get(position).getImages().size())); //String.format(holder.files.getContext().getString(R.string.gallery_image_count_format), data.get(folders[position]).size()));

        holder.selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderClickListener != null)
                    folderClickListener.onFolderClick(folders.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class FoldersViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private View selector;
        private TextView name;
        private TextView files;

        public FoldersViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            selector = itemView.findViewById(R.id.selector);
            name = (TextView) itemView.findViewById(R.id.name);
            files = (TextView) itemView.findViewById(R.id.num);
        }
    }

    public void setData(List<Folder> folders) {
        this.folders = folders;

        notifyDataSetChanged();
    }

    public void setImageSize(int size) {
        this.size = size;
        imageSize = size - padding * 2;
    }
}
