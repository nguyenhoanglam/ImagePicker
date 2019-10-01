package com.nguyenhoanglam.imagepicker.ui.camera;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import com.nguyenhoanglam.imagepicker.helper.ImageHelper;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.util.FileUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by hoanglam on 8/18/17.
 */

public class DefaultCameraModule implements CameraModule, Serializable {

    private String imagePath;


    @Override
    public Intent getCameraIntent(Context context, Config config) {
        Intent intent;
        File assetFile = null;
        if (config.isIncludeVideos()) {
            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent = Intent.createChooser(imgIntent, "Campture Image or Video");
            intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{videoIntent});
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            assetFile = new ImageHelper().createAssetFile(false, config.getSavePath());
        }
        if (assetFile != null) {
            Context appContext = context.getApplicationContext();
            String providerName = String.format(Locale.ENGLISH, "%s%s", appContext.getPackageName(), ".fileprovider");
            Uri uri = FileProvider.getUriForFile(appContext, providerName, assetFile);
            imagePath = assetFile.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ImageHelper.grantAppPermission(context, intent, uri);
        }
        return intent;
    }

    @Override
    public void getImage(final Context context, Intent intent, final OnAssetReadyListener imageReadyListener) {
        if (imageReadyListener == null) {
            throw new IllegalStateException("OnAssetReadyListener must not be null");
        }

        if (imagePath == null) {
            imagePath = FileUtils.getPath(context, intent.getData());
        }

        final Uri imageUri = Uri.parse(new File(imagePath).toString());
        if (imageUri != null) {
            MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{imageUri.getPath()}
                    , null
                    , new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (path == null) {
                                path = imagePath;
                            }
                            imageReadyListener.onAssetReady(ImageHelper.singleListFromPath(path));
                            ImageHelper.revokeAppPermission(context, imageUri);
                        }
                    });
        }
    }
}
