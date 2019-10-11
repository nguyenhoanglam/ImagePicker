package com.nguyenhoanglam.imagepicker.ui.imagepicker;

import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Folder;
import com.nguyenhoanglam.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/17/17.
 */

public interface ImagePickerView extends MvpView {

    void showLoading(boolean isLoading);

    void showFetchCompleted(List<Asset> assets, List<Folder> folders);

    void showError(Throwable throwable);

    void showEmpty();

    void showCapturedAsset(List<Asset> assets);

    void finishPickAssets(List<Asset> assets);

}