package com.nguyenhoanglam.imagepicker.ui.camera;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import com.nguyenhoanglam.imagepicker.helper.ImageHelper;
import com.nguyenhoanglam.imagepicker.model.Config;

import java.io.File;
import java.io.Serializable;
import java.util.Locale;

/**
 * Created by hoanglam on 8/18/17.
 */

public class DefaultCameraModule implements CameraModule, Serializable {

    protected String imagePath;


    @Override
    public Intent getCameraIntent(Context context, Config config) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new ImageHelper().createImageFile(config.getSavePath());
        if (imageFile != null) {
            Context appContext = context.getApplicationContext();
            String providerName = String.format(Locale.ENGLISH, "%s%s", appContext.getPackageName(), ".fileprovider");
            Uri uri = FileProvider.getUriForFile(appContext, providerName, imageFile);
            imagePath = imageFile.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ImageHelper.grantAppPermission(context, intent, uri);
            return intent;
        }
        return null;
    }

    @Override
    public void getImage(final Context context, Intent intent, final OnImageReadyListener imageReadyListener) {
        if (imageReadyListener == null) {
            throw new IllegalStateException("OnImageReadyListener must not be null");
        }

        if (imagePath == null) {
            imageReadyListener.onImageReady(null);
            return;
        }

        final Uri imageUri = Uri.parse(new File(imagePath).toString());
        if (imageUri != null) {
            MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{imageUri.getPath()}
                    , null
                    , new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (path != null) {
                                path = imagePath;
                            }
                            imageReadyListener.onImageReady(ImageHelper.singleListFromPath(path));
                            ImageHelper.revokeAppPermission(context, imageUri);
                        }
                    });
        }
    }
}
