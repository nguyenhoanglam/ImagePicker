package com.nguyenhoanglam.imagepicker.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/11/17.
 */


public class Config implements Parcelable {

    public static final String EXTRA_CONFIG = "ImagePickerConfig";
    public static final String EXTRA_IMAGES = "ImagePickerImages";


    public static final int RC_PICK_IMAGES = 100;
    public static final int RC_CAPTURE_IMAGE = 101;
    public static final int RC_WRITE_EXTERNAL_STORAGE_PERMISSION = 102;
    public static final int RC_CAMERA_PERMISSION = 103;


    public static final int MAX_SIZE = Integer.MAX_VALUE;
    public static final Parcelable.Creator<Config> CREATOR = new Parcelable.Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel source) {
            return new Config(source);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };
    private String toolbarColor;
    private String statusBarColor;
    private String toolbarTextColor;
    private String toolbarIconColor;
    private String progressBarColor;
    private String backgroundColor;
    private boolean isCameraOnly;
    private boolean isMultipleMode;
    private boolean isFolderMode;
    private boolean isShowCamera;
    private int maxSize;
    private String doneTitle;
    private String folderTitle;
    private String imageTitle;
    private String limitMessage;
    private SavePath savePath;
    private boolean isAlwaysShowDoneButton;
    private boolean isKeepScreenOn;
    private int requestCode;
    private ArrayList<Image> selectedImages;


    public Config() {
    }

    protected Config(Parcel in) {
        this.toolbarColor = in.readString();
        this.statusBarColor = in.readString();
        this.toolbarTextColor = in.readString();
        this.toolbarIconColor = in.readString();
        this.progressBarColor = in.readString();
        this.backgroundColor = in.readString();
        this.isCameraOnly = in.readByte() != 0;
        this.isMultipleMode = in.readByte() != 0;
        this.isFolderMode = in.readByte() != 0;
        this.isShowCamera = in.readByte() != 0;
        this.maxSize = in.readInt();
        this.doneTitle = in.readString();
        this.folderTitle = in.readString();
        this.imageTitle = in.readString();
        this.limitMessage = in.readString();
        this.savePath = in.readParcelable(SavePath.class.getClassLoader());
        this.isAlwaysShowDoneButton = in.readByte() != 0;
        this.isKeepScreenOn = in.readByte() != 0;
        this.requestCode = in.readInt();
        this.selectedImages = in.createTypedArrayList(Image.CREATOR);
    }

    public int getToolbarColor() {
        if (TextUtils.isEmpty(toolbarColor)) {
            return Color.parseColor("#212121");
        }
        return Color.parseColor(toolbarColor);
    }

    public void setToolbarColor(String toolbarColor) {
        this.toolbarColor = toolbarColor;
    }

    public int getStatusBarColor() {
        if (TextUtils.isEmpty(statusBarColor)) {
            return Color.parseColor("#000000");
        }
        return Color.parseColor(statusBarColor);
    }

    public void setStatusBarColor(String statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public int getToolbarTextColor() {
        if (TextUtils.isEmpty(toolbarTextColor)) {
            return Color.parseColor("#FFFFFF");
        }
        return Color.parseColor(toolbarTextColor);
    }

    public void setToolbarTextColor(String toolbarTextColor) {
        this.toolbarTextColor = toolbarTextColor;
    }

    public int getToolbarIconColor() {
        if (TextUtils.isEmpty(toolbarIconColor)) {
            return Color.parseColor("#FFFFFF");
        }
        return Color.parseColor(toolbarIconColor);
    }

    public void setToolbarIconColor(String toolbarIconColor) {
        this.toolbarIconColor = toolbarIconColor;
    }

    public int getProgressBarColor() {
        if (TextUtils.isEmpty(progressBarColor)) {
            return Color.parseColor("#4CAF50");
        }
        return Color.parseColor(progressBarColor);
    }

    public void setProgressBarColor(String progressBarColor) {
        this.progressBarColor = progressBarColor;
    }

    public int getBackgroundColor() {
        if (TextUtils.isEmpty(backgroundColor)) {
            return Color.parseColor("#212121");
        }
        return Color.parseColor(backgroundColor);
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isCameraOnly() {
        return isCameraOnly;
    }

    public void setCameraOnly(boolean cameraOnly) {
        isCameraOnly = cameraOnly;
    }

    public boolean isMultipleMode() {
        return isMultipleMode;
    }

    public void setMultipleMode(boolean multipleMode) {
        isMultipleMode = multipleMode;
    }

    public boolean isFolderMode() {
        return isFolderMode;
    }

    public void setFolderMode(boolean folderMode) {
        isFolderMode = folderMode;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getDoneTitle() {
        return doneTitle;
    }

    public void setDoneTitle(String doneTitle) {
        this.doneTitle = doneTitle;
    }

    public String getFolderTitle() {
        return folderTitle;
    }

    public void setFolderTitle(String folderTitle) {
        this.folderTitle = folderTitle;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getLimitMessage() {
        return limitMessage;
    }

    public void setLimitMessage(String limitMessage) {
        this.limitMessage = limitMessage;
    }

    public SavePath getSavePath() {
        return savePath;
    }

    public void setSavePath(SavePath savePath) {
        this.savePath = savePath;
    }

    public boolean isAlwaysShowDoneButton() {
        return isAlwaysShowDoneButton;
    }

    public void setAlwaysShowDoneButton(boolean isAlwaysShowDoneButton) {
        this.isAlwaysShowDoneButton = isAlwaysShowDoneButton;
    }

    public boolean isKeepScreenOn() {
        return isKeepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        isKeepScreenOn = keepScreenOn;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public ArrayList<Image> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(ArrayList<Image> selectedImages) {
        this.selectedImages = selectedImages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.toolbarColor);
        dest.writeString(this.statusBarColor);
        dest.writeString(this.toolbarTextColor);
        dest.writeString(this.toolbarIconColor);
        dest.writeString(this.progressBarColor);
        dest.writeString(this.backgroundColor);
        dest.writeByte(this.isCameraOnly ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMultipleMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFolderMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowCamera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSize);
        dest.writeString(this.doneTitle);
        dest.writeString(this.folderTitle);
        dest.writeString(this.imageTitle);
        dest.writeString(this.limitMessage);
        dest.writeParcelable(this.savePath, flags);
        dest.writeByte(this.isAlwaysShowDoneButton ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isKeepScreenOn ? (byte) 1 : (byte) 0);
        dest.writeInt(this.requestCode);
        dest.writeTypedList(this.selectedImages);
    }
}

