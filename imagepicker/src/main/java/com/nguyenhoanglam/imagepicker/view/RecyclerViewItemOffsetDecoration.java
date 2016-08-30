package com.nguyenhoanglam.imagepicker.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by hoanglam on 8/24/16.
 */
public class RecyclerViewItemOffsetDecoration extends RecyclerView.ItemDecoration {

    private int mItemOffset;

    public RecyclerViewItemOffsetDecoration(int itemOffset) {
        mItemOffset = itemOffset;
    }

    public RecyclerViewItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
        this(context.getResources().getDimensionPixelSize(itemOffsetId));

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
    }
}