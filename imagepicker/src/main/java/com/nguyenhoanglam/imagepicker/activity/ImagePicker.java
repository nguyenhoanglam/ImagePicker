/*
 * Created by Nguyen Hoang Lam
 * Date: ${DATE}
 */

package com.nguyenhoanglam.imagepicker.activity;

import android.app.Activity;
import android.content.Intent;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.helper.Constants;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/4/16.
 */
public class ImagePicker {

    private Activity activity;
    private int mode;
    private int limit;
    private boolean showCamera;
    private String title;
    private ArrayList<Image> selectedImages;
    private boolean folderMode;

    public ImagePicker(Activity activity) {
        this.activity = activity;
        this.mode = ImagePickerActivity.MODE_MULTIPLE;
        this.limit = Constants.MAX_LIMIT;
        this.showCamera = true;
        this.title = activity.getString(R.string.title_select_image);
        this.selectedImages = new ArrayList<>();
        this.folderMode = false;
    }


    public static ImagePicker create(Activity activity) {
        return new ImagePicker(activity);
    }

    public ImagePicker single() {
        mode = ImagePickerActivity.MODE_SINGLE;
        return this;
    }

    public ImagePicker multi() {
        mode = ImagePickerActivity.MODE_MULTIPLE;
        return this;
    }


    public ImagePicker limit(int count) {
        limit = count;
        return this;
    }

    public ImagePicker showCamera(boolean show) {
        showCamera = show;
        return this;
    }

    public ImagePicker title(String title) {
        this.title = title;
        return this;
    }

    public ImagePicker origin(ArrayList<Image> images) {
        selectedImages = images;
        return this;
    }

    public ImagePicker folderMode(boolean folderMode) {
        this.folderMode = folderMode;
        return this;
    }

    public void start(int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, mode);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, limit);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_TITLE, title);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, folderMode);

        activity.startActivityForResult(intent, requestCode);
    }

}
