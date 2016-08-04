package com.nguyenhoanglam.imagepicker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hoanglam on 7/31/16.
 */
public class Image implements Parcelable {

    public long id;
    public String name;
    public String path;
    public boolean isSelected;

    public Image(long id, String name, String path, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected Image(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.path = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
