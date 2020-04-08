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

    private String toolbarColor;
    private String statusBarColor;
    private String toolbarTextColor;
    private String toolbarIconColor;
    private String progressBarColor;
    private String backgroundColor;
    private boolean isCameraOnly;
    private boolean isMultipleMode;
    private boolean isFolderMode;
    private boolean isShowSelectedAsNumber;
    private boolean isShowCamera;
    private int maxSize;
    private String doneTitle;
    private String folderTitle;
    private String imageTitle;
    private String limitMessage;
    private String directoryName;
    private boolean isAlwaysShowDoneButton;
    private int requestCode;
    private ArrayList<Image> selectedImages;


    public Config() {
    }

    protected Config(Parcel in) {
        toolbarColor = in.readString();
        statusBarColor = in.readString();
        toolbarTextColor = in.readString();
        toolbarIconColor = in.readString();
        progressBarColor = in.readString();
        backgroundColor = in.readString();
        isCameraOnly = in.readByte() != 0;
        isMultipleMode = in.readByte() != 0;
        isFolderMode = in.readByte() != 0;
        isShowSelectedAsNumber = in.readByte() != 0;
        isShowCamera = in.readByte() != 0;
        maxSize = in.readInt();
        doneTitle = in.readString();
        folderTitle = in.readString();
        imageTitle = in.readString();
        limitMessage = in.readString();
        directoryName = in.readString();
        isAlwaysShowDoneButton = in.readByte() != 0;
        requestCode = in.readInt();
        selectedImages = in.createTypedArrayList(Image.CREATOR);
    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel in) {
            return new Config(in);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };

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

    public boolean isShowSelectedAsNumber() {
        return isShowSelectedAsNumber;
    }

    public void setShowSelectedAsNumber(boolean showSelectedAsNumber) {
        isShowSelectedAsNumber = showSelectedAsNumber;
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

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public boolean isAlwaysShowDoneButton() {
        return isAlwaysShowDoneButton;
    }

    public void setAlwaysShowDoneButton(boolean isAlwaysShowDoneButton) {
        this.isAlwaysShowDoneButton = isAlwaysShowDoneButton;
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
        dest.writeString(toolbarColor);
        dest.writeString(statusBarColor);
        dest.writeString(toolbarTextColor);
        dest.writeString(toolbarIconColor);
        dest.writeString(progressBarColor);
        dest.writeString(backgroundColor);
        dest.writeByte((byte) (isCameraOnly ? 1 : 0));
        dest.writeByte((byte) (isMultipleMode ? 1 : 0));
        dest.writeByte((byte) (isFolderMode ? 1 : 0));
        dest.writeByte((byte) (isShowSelectedAsNumber ? 1 : 0));
        dest.writeByte((byte) (isShowCamera ? 1 : 0));
        dest.writeInt(maxSize);
        dest.writeString(doneTitle);
        dest.writeString(folderTitle);
        dest.writeString(imageTitle);
        dest.writeString(limitMessage);
        dest.writeString(directoryName);
        dest.writeByte((byte) (isAlwaysShowDoneButton ? 1 : 0));
        dest.writeInt(requestCode);
        dest.writeTypedList(selectedImages);
    }
}

