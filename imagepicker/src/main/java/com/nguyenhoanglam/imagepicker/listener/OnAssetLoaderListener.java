package com.nguyenhoanglam.imagepicker.listener;

import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Folder;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.List;

/**
 * Created by hoanglam on 8/17/17.
 */

public interface OnAssetLoaderListener {
    void onAssetLoaded(List<Asset> assets, List<Folder> folders);

    void onFailed(Throwable throwable);
}
