package com.nguyenhoanglam.imagepicker.ui.camera;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.nguyenhoanglam.imagepicker.helper.ImageHelper;
import com.nguyenhoanglam.imagepicker.helper.LogHelper;
import com.nguyenhoanglam.imagepicker.model.Config;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hoanglam on 8/18/17.
 */

public class DefaultCameraModule implements CameraModule, Serializable {

    protected String imageFilePath;


    @Override
    public Intent getCameraIntent(Context context, Config config) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = createImageFileUri(context, config.getDirectoryName());
        LogHelper.getInstance().d("Created image URI " + uri);
        if (uri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ImageHelper.grantAppPermission(context, intent, uri);
            return intent;
        }
        return null;
    }

    public Uri createImageFileUri(Context context, String directory) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = "DCIM/" + directory;
        String fileName = "IMG_" + timeStamp + ".jpg";

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            imageFilePath = uri.getPath();
        } else {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File imageFile = new File(dir, "/" + directory + "/" + fileName);
            File parentFile = imageFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }
            Context appContext = context.getApplicationContext();
            String providerName = appContext.getPackageName() + ".fileprovider";
            uri = FileProvider.getUriForFile(appContext, providerName, imageFile);
            imageFilePath = imageFile.getAbsolutePath();
        }
        return uri;
    }

    @Override
    public void getImage(final Context context, Intent intent, final OnImageReadyListener imageReadyListener) {
        if (imageReadyListener == null) {
            throw new IllegalStateException("OnImageReadyListener must not be null");
        }

        if (imageFilePath == null) {
            imageReadyListener.onImageReady(null);
            return;
        }

        if (imageFilePath != null) {
            MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{imageFilePath}
                    , null
                    , new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (path != null) {
                                path = imageFilePath;
                            }
                            imageReadyListener.onImageReady(ImageHelper.singleListFromPath(path));
                            ImageHelper.revokeAppPermission(context, uri);
                            imageFilePath = null;
                        }
                    });
        }
    }
}
