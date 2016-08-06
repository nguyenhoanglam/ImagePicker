/*
 * Created by Nguyen Hoang Lam
 * Date: ${DATE}
 */

package com.nguyenhoanglam.imagepicker.activity;

import android.app.Activity;
import android.content.Intent;

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
    private ArrayList<Image> selectedImages;

    private static ImagePicker sImagePicker;

    public ImagePicker(Activity activity) {
        this.activity = activity;
        this.mode = ImagePickerActivity.MODE_MULTIPLE;
        this.limit = Constants.MAX_LIMIT;
        this.showCamera = true;
        this.selectedImages = new ArrayList<>();
    }


    public static ImagePicker create(Activity activity) {
        if (sImagePicker == null)
            sImagePicker = new ImagePicker(activity);
        return sImagePicker;
    }

    public ImagePicker single() {
        mode = ImagePickerActivity.MODE_SINGLE;
        return sImagePicker;
    }

    public ImagePicker multi() {
        mode = ImagePickerActivity.MODE_MULTIPLE;
        return sImagePicker;
    }


    public ImagePicker limit(int count) {
        limit = count;
        return sImagePicker;
    }

    public ImagePicker showCamera(boolean show) {
        showCamera = show;
        return sImagePicker;
    }

    public ImagePicker origin(ArrayList<Image> images) {
        selectedImages = images;
        return sImagePicker;
    }

    public void start(int requestCode) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, mode);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, limit);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);

        activity.startActivityForResult(intent, requestCode);
    }

}
