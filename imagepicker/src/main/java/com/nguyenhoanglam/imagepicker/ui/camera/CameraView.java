package com.nguyenhoanglam.imagepicker.ui.camera;

import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/22/17.
 */

public interface CameraView extends MvpView {

    void finishPickAssets(List<Asset> assets);
}
