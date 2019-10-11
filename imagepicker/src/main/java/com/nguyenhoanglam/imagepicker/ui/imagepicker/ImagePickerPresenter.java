package com.nguyenhoanglam.imagepicker.ui.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.listener.OnAssetLoaderListener;
import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Folder;
import com.nguyenhoanglam.imagepicker.ui.camera.CameraModule;
import com.nguyenhoanglam.imagepicker.ui.camera.DefaultCameraModule;
import com.nguyenhoanglam.imagepicker.ui.camera.OnAssetReadyListener;
import com.nguyenhoanglam.imagepicker.ui.common.BasePresenter;

import java.io.File;
import java.util.List;

/**
 * Created by hoanglam on 8/17/17.
 */

public class ImagePickerPresenter extends BasePresenter<ImagePickerView> {

    private AssetFileLoader imageLoader;
    private CameraModule cameraModule = new DefaultCameraModule();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ImagePickerPresenter(AssetFileLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void abortLoading() {
        imageLoader.abortLoadAssets();
    }

    public void loadAssets(boolean includeVideos, boolean isFolderMode) {
        if (!isViewAttached()) return;

        getView().showLoading(true);
        imageLoader.loadDeviceAssets(includeVideos, isFolderMode, new OnAssetLoaderListener() {
            @Override
            public void onAssetLoaded(final List<Asset> assets, final List<Folder> folders) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().showFetchCompleted(assets, folders);
                            final boolean isEmpty = folders != null ? folders.isEmpty() : assets.isEmpty();
                            if (isEmpty) {
                                getView().showEmpty();
                            } else {
                                getView().showLoading(false);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailed(final Throwable throwable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isViewAttached()) {
                            getView().showError(throwable);
                        }
                    }
                });
            }
        });
    }

    void captureImage(Activity activity, Config config, int requestCode) {
        Context context = activity.getApplicationContext();
        Intent intent = cameraModule.getCameraIntent(activity, config);
        if (intent == null) {
            Toast.makeText(context, context.getString(R.string.imagepicker_error_create_image_file), Toast.LENGTH_LONG).show();
            return;
        }
        activity.startActivityForResult(intent, requestCode);
    }

    public void finishCaptureImage(Context context, Intent data, final Config config) {
        cameraModule.getImage(context, data, new OnAssetReadyListener() {
            @Override
            public void onAssetReady(List<Asset> assets) {
                if (!config.isMultipleMode()) {
                    getView().finishPickAssets(assets);
                } else {
                    getView().showCapturedAsset(assets);
                }
            }
        });
    }

    public void onDoneSelectAssets(List<Asset> selectedAssets) {
        if (selectedAssets != null && !selectedAssets.isEmpty()) {
            for (int i = 0; i < selectedAssets.size(); i++) {
                Asset asset = selectedAssets.get(i);
                File file = new File(asset.getPath());
                if (!file.exists()) {
                    selectedAssets.remove(i);
                    i--;
                }
            }
        }
        getView().finishPickAssets(selectedAssets);
    }

}
