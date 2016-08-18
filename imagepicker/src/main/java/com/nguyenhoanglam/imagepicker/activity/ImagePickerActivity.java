package com.nguyenhoanglam.imagepicker.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.adapter.ImagePickerAdapter;
import com.nguyenhoanglam.imagepicker.helper.Constants;
import com.nguyenhoanglam.imagepicker.helper.ImageUtils;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hoanglam on 7/31/16.
 */
public class ImagePickerActivity extends AppCompatActivity implements ImagePickerAdapter.ViewHolder.OnItemClickListener {

    private static final String TAG = "ImagePickerActivity";

    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTIPLE = 2;

    public static final String INTENT_EXTRA_SELECTED_IMAGES = "selectedImages";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final String INTENT_EXTRA_SHOW_CAMERA = "showCamera";
    public static final String INTENT_EXTRA_MODE = "mode";
    public static final String INTENT_EXTRA_TITLE = "title";


    private ArrayList<Image> images;
    private File fileTemp;
    private Uri fileUri;

    private ArrayList<Image> selectedImages;
    private boolean showCamera;
    private int mode;
    private int limit;
    private String title;

    private ActionBar actionBar;

    private MenuItem menuAdd, menuCamera;
    private final int menuAddId = 100;
    private final int menuCameraId = 101;

    private RelativeLayout mainLayout;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView emptyTextView;

    private ImagePickerAdapter adapter;


    private ContentObserver observer;
    private Handler handler;
    private Thread thread;

    private final String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
            actionBar.setDisplayShowTitleEnabled(true);
        }


        limit = intent.getIntExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, Constants.MAX_LIMIT);
        mode = intent.getIntExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        title = intent.getStringExtra(ImagePickerActivity.INTENT_EXTRA_TITLE);
        showCamera = intent.getBooleanExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        if (mode == ImagePickerActivity.MODE_MULTIPLE && intent.hasExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES)) {
            selectedImages = intent.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
        }

        if (selectedImages == null)
            selectedImages = new ArrayList<>();
        images = new ArrayList<>();

        // Set default toolbar title
        if (actionBar != null)
            actionBar.setTitle(title);

        adapter = new ImagePickerAdapter(this, images, selectedImages, this);


        mainLayout = (RelativeLayout) findViewById(R.id.main);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        emptyTextView = (TextView) findViewById(R.id.tv_empty_images);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        orientationBasedUI(getResources().getConfiguration().orientation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataWithPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu.findItem(menuCameraId) == null) {
            menuCamera = menu.add(Menu.NONE, menuCameraId, 1, getString(R.string.camera));
            menuCamera.setIcon(R.drawable.ic_camera_white);
            menuCamera.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            menuCamera.setVisible(showCamera);
        }

        if (menu.findItem(menuAddId) == null) {
            menuAdd = menu.add(Menu.NONE, menuAddId, 2, getString(R.string.add));
            menuAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        }

        updateTitle();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        if (id == menuAddId) {
            if (selectedImages != null && selectedImages.size() > 0) {
                for (int i = 0; i < selectedImages.size(); i++) {
                    Image image = selectedImages.get(i);
                    File file = new File(image.getPath());
                    if (file == null || !file.exists()) {
                        selectedImages.remove(i);
                        i--;
                    }
                }
                Intent data = new Intent();
                data.putParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
                setResult(RESULT_OK, data);
                finish();
            }
            return true;
        }
        if (id == menuCameraId) {
            captureImage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        int columns = orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;

        GridLayoutManager layoutManager = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(layoutManager);

        if (adapter != null) {
            int size = metrics.widthPixels / columns;
            adapter.setImageSize(size);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Write External permission granted");
            refreshData();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_select_image))
                .setMessage(R.string.msg_no_write_external_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void getDataWithPermission() {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED)
            refreshData();
        else
            requestWriteExternalPermission();
    }

    private void refreshData() {
        abortLoading();

        ImageLoaderRunnable runnable = new ImageLoaderRunnable();
        thread = new Thread(runnable);
        thread.start();
    }

    private void requestWriteExternalPermission() {
        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        };

        Snackbar.make(mainLayout, R.string.msg_no_write_external_permission,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();

    }

    @Override
    public void onClick(View view, int position) {
        clickImage(position);
    }

    private void clickImage(int position) {
        if (mode == ImagePickerActivity.MODE_MULTIPLE) {
            if (!selectedImages.contains(images.get(position))) {
                if (selectedImages.size() < limit) {
                    adapter.addSelected(images.get(position));
                } else {
                    Toast.makeText(this, R.string.msg_limit_images, Toast.LENGTH_SHORT).show();
                }
            } else {
                adapter.removeSelected(images.get(position));
            }
        } else {
            if (selectedImages.contains(images.get(position)))
                adapter.removeSelected(images.get(position));
            else {
                for (int i = 0; i < selectedImages.size(); i++)
                    adapter.removeSelected(selectedImages.get(i));
                adapter.addSelected(images.get(position));
            }
        }
        updateTitle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                if (fileTemp != null && fileTemp.exists()) {
                    MediaScannerConnection.scanFile(this,
                            new String[]{fileTemp.getAbsolutePath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.v("MediaScanWork", "file " + path
                                            + " was scanned successfully: " + uri);
                                    getDataWithPermission();
                                }
                            });
                }
            }
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            fileTemp = ImageUtils.getOutputMediaFile();
            if (fileTemp != null) {
                fileUri = Uri.fromFile(fileTemp);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, Constants.REQUEST_CODE_CAPTURE);
            } else {
                Toast.makeText(this, getString(R.string.error_create_image_file), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.error_no_camera), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.FETCH_STARTED: {
                        showLoading();
                        break;
                    }
                    case Constants.FETCH_COMPLETED: {
                        ArrayList<Image> temps = new ArrayList<>();
                        temps.addAll(selectedImages);

                        ArrayList<Image> newImages = new ArrayList<>();
                        newImages.addAll(images);

                        adapter.clear();
                        adapter.addAll(newImages);
                        recyclerView.setAdapter(adapter);

                        if (images.size() != 0)
                            hideLoading();
                        else
                            showEmpty();

                        for (int j = 0; j < temps.size(); j++) {
                            for (int i = 0; i < images.size(); i++) {
                                if (fileTemp != null && fileTemp.exists())
                                    if (images.get(i).getPath().equals(fileTemp.getPath())) {
                                        temps.add(new Image(0, "", fileTemp.getPath(), false));
                                        fileTemp = null;
                                    }

                                if (images.get(i).getPath().equals(temps.get(j).getPath())) {
                                    clickImage(i);
                                }
                            }
                        }

                        break;
                    }
                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                refreshData();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
    }


    private void abortLoading() {
        if (thread == null)
            return;
        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateTitle() {
        if (selectedImages.size() == 0) {
            actionBar.setTitle(title);
            if (menuAdd != null)
                menuAdd.setVisible(false);
        } else {
            if (mode == ImagePickerActivity.MODE_MULTIPLE) {
                if (limit == Constants.MAX_LIMIT)
                    actionBar.setTitle(String.format(getString(R.string.selected), selectedImages.size()));
                else
                    actionBar.setTitle(String.format(getString(R.string.selected_with_limit), selectedImages.size(), limit));
            }
            if (menuAdd != null)
                menuAdd.setVisible(true);
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        abortLoading();

        getContentResolver().unregisterContentObserver(observer);

        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }


    private class ImageLoaderRunnable implements Runnable {

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            Message message;
            if (adapter == null) {
                /*
                If the adapter is null, this is first time this activity's view is
                being shown, hence send FETCH_STARTED message to show progress bar
                while images are loaded from phone
                 */
                message = handler.obtainMessage();
                message.what = Constants.FETCH_STARTED;
                message.sendToTarget();
            }

            if (Thread.interrupted()) {
                return;
            }

            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null) {
                message = handler.obtainMessage();
                message.what = Constants.ERROR;
                message.sendToTarget();
                return;
            }

            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            File file;

            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));

                    file = new File(path);
                    if (file.exists()) {
                        Image image = new Image(id, name, path, false);
                        temp.add(image);
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();
            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            message = handler.obtainMessage();
            message.what = Constants.FETCH_COMPLETED;
            message.sendToTarget();

            Thread.interrupted();

        }
    }

}
