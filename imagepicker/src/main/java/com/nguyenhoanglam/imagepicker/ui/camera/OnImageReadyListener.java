package com.nguyenhoanglam.imagepicker.ui.camera;


import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

public interface OnImageReadyListener {
    void onImageReady(List<Image> images);
}
