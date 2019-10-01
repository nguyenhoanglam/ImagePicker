package com.nguyenhoanglam.imagepicker.listener;

import com.nguyenhoanglam.imagepicker.model.Asset;

import java.util.List;

/**
 * Created by hoanglam on 8/18/17.
 */

public interface OnAssetSelectionListener {
    void onSelectionUpdate(List<Asset> assets);
}
