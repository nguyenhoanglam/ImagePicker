package com.nguyenhoanglam.imagepicker.widget;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nguyenhoanglam.imagepicker.R;

/**
 * Created by hoanglam on 8/21/17.
 */

public class SnackBarView extends RelativeLayout {

    private static final int ANIM_DURATION = 200;

    private static final Interpolator INTERPOLATOR = new FastOutLinearInInterpolator();

    private TextView messageText;
    private Button actionButton;

    private boolean isShowing;

    public SnackBarView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SnackBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SnackBarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.imagepicker_snackbar, this);
        if (isInEditMode()) {
            return;
        }

        setBackgroundColor(Color.parseColor("#323232"));
        setTranslationY(getHeight());
        setAlpha(0f);
        isShowing = false;

        int horizontalPadding = convertDpToPixels(context, 24f);
        int verticalPadding = convertDpToPixels(context, 14);
        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);

        messageText = findViewById(R.id.text_snackbar_message);
        actionButton = findViewById(R.id.button_snackbar_action);
    }

    private void setText(int textResId) {
        messageText.setText(textResId);
    }

    private void setOnActionClickListener(String actionText, final OnClickListener onClickListener) {
        actionButton.setText(actionText);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                hide(new Runnable() {
                    @Override
                    public void run() {
                        onClickListener.onClick(view);
                    }
                });
            }
        });
    }

    public void show(int textResId, OnClickListener onClickListener) {
        setText(textResId);
        setOnActionClickListener(getContext().getString(R.string.imagepicker_action_ok), onClickListener);

        ViewCompat.animate(this)
                .translationY(0f)
                .setDuration(ANIM_DURATION)
                .setInterpolator(INTERPOLATOR)
                .alpha(1f);

        isShowing = true;
    }

    private void hide(Runnable runnable) {
        ViewCompat.animate(this)
                .translationY(getHeight())
                .setDuration(ANIM_DURATION)
                .alpha(0.5f)
                .withEndAction(runnable);

        isShowing = false;
    }

    public boolean isShowing() {
        return isShowing;
    }

    private int convertDpToPixels(Context context, float dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return px;
    }
}
