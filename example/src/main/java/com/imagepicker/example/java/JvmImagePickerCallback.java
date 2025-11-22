package com.imagepicker.example.java;

import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public interface JvmImagePickerCallback extends Function1<ArrayList<Image>, Unit> {

    void onImagePickerResult(ArrayList<Image> images);

    @Override
    default Unit invoke(ArrayList<Image> images) {
        this.onImagePickerResult(images);
        return null;
    }
}
