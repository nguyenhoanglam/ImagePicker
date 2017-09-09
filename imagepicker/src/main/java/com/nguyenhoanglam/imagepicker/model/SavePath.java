package com.nguyenhoanglam.imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hoanglam on 8/18/17.
 */

public class SavePath implements Parcelable {

    public static final SavePath DEFAULT = new SavePath("Camera", false);

    public static final Creator<SavePath> CREATOR = new Creator<SavePath>() {
        @Override
        public SavePath createFromParcel(Parcel source) {
            return new SavePath(source);
        }

        @Override
        public SavePath[] newArray(int size) {
            return new SavePath[size];
        }
    };
    private final String path;
    private final boolean isFullPath;

    public SavePath(String path, boolean isFullPath) {
        this.path = path;
        this.isFullPath = isFullPath;
    }

    protected SavePath(Parcel in) {
        this.path = in.readString();
        this.isFullPath = in.readByte() != 0;
    }

    public String getPath() {
        return path;
    }

    public boolean isFullPath() {
        return isFullPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeByte(this.isFullPath ? (byte) 1 : (byte) 0);
    }
}
