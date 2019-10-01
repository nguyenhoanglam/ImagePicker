package com.nguyenhoanglam.imagepicker.ui.imagepicker;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.nguyenhoanglam.imagepicker.listener.OnAssetLoaderListener;
import com.nguyenhoanglam.imagepicker.model.Asset;
import com.nguyenhoanglam.imagepicker.model.Folder;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.Video;
import com.nguyenhoanglam.imagepicker.util.Extensions_FileKt;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hoanglam on 8/17/17.
 */

public class AssetFileLoader {

    private final String[] projection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
    };

    private Context context;
    private ExecutorService executorService;

    public AssetFileLoader(Context context) {
        this.context = context;
    }

    private static File makeSafeFile(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        try {
            return new File(path);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void loadDeviceAssets(boolean includeVideos, boolean isFolderMode, OnAssetLoaderListener listener) {
        getExecutorService().execute(new AssetLoadRunnable(includeVideos, isFolderMode, listener));
    }

    public void abortLoadAssets() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(5);
//            executorService = Executors.newSingleThreadExecutor();
        }
        return executorService;
    }

    private class AssetLoadRunnable implements Runnable {

        private boolean isFolderMode;
        private boolean includeVideos;
        private OnAssetLoaderListener listener;

        public AssetLoadRunnable(boolean includeVideos, boolean isFolderMode, OnAssetLoaderListener listener) {
            this.includeVideos = includeVideos;
            this.isFolderMode = isFolderMode;
            this.listener = listener;
        }

        @Override
        public void run() {
            String selectionImages = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

            String selectionImagesAndVideo = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            Uri queryUri = MediaStore.Files.getContentUri("external");

            Cursor cursor = context.getContentResolver().query(
                    queryUri,
                    projection,
                    includeVideos ? selectionImagesAndVideo : selectionImages,
                    null,
                    MediaStore.Files.FileColumns.DATE_ADDED
            );

            if (cursor == null) {
                listener.onFailed(new NullPointerException());
                return;
            }

            List<Asset> assets = new ArrayList<>(cursor.getCount());
            Map<String, Folder> folderMap = isFolderMode ? new LinkedHashMap<String, Folder>() : null;

            if (cursor.moveToLast()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    String bucket = cursor.getString(cursor.getColumnIndex(projection[3]));

                    File file = makeSafeFile(path);
                    if (file != null && file.exists()) {

                        // Check if file is an image or a video
                        Asset asset = null;
                        if (Extensions_FileKt.isImageFile(file)) { // If file is an Image
                            asset = new Image(id, name, path);
                            assets.add(asset);
                        } else if (Extensions_FileKt.isVideoFile(file)) { // If file is a Video
                            asset = new Video(id, name, path);
                            assets.add(asset);
                        }

                        if (folderMap != null) {
                            Folder folder = folderMap.get(bucket);
                            if (folder == null) {
                                folder = new Folder(bucket);
                                folderMap.put(bucket, folder);
                            }
                            folder.getImages().add(asset);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            /* Convert HashMap to ArrayList if not null */
            List<Folder> folders = null;
            if (folderMap != null) {
                folders = new ArrayList<>(folderMap.values());
            }

            listener.onAssetLoaded(assets, folders);
        }
    }
}
