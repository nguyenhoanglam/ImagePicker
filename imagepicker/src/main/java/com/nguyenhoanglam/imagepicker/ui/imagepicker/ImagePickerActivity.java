package com.nguyenhoanglam.imagepicker.ui.imagepicker;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.helper.CameraHelper;
import com.nguyenhoanglam.imagepicker.helper.LogHelper;
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper;
import com.nguyenhoanglam.imagepicker.listener.OnBackAction;
import com.nguyenhoanglam.imagepicker.listener.OnFolderClickListener;
import com.nguyenhoanglam.imagepicker.listener.OnImageClickListener;
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectionListener;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Folder;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.widget.ImagePickerToolbar;
import com.nguyenhoanglam.imagepicker.widget.ProgressWheel;
import com.nguyenhoanglam.imagepicker.widget.SnackBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoanglam on 7/31/16.
 */
public class ImagePickerActivity extends AppCompatActivity implements ImagePickerView {

    private ImagePickerToolbar toolbar;
    private RecyclerViewManager recyclerViewManager;
    private RecyclerView recyclerView;
    private ProgressWheel progressWheel;
    private View emptyLayout;
    private SnackBarView snackBar;

    private Config config;
    private Handler handler;
    private ContentObserver observer;
    private ImagePickerPresenter presenter;
    private LogHelper logger = LogHelper.getInstance();


    private OnImageClickListener imageClickListener = new OnImageClickListener() {
        @Override
        public boolean onImageClick(View view, int position, boolean isSelected) {
            return recyclerViewManager.selectImage();
        }
    };

    private OnFolderClickListener folderClickListener = new OnFolderClickListener() {
        @Override
        public void onFolderClick(Folder folder) {
            setImageAdapter(folder.getImages(), folder.getFolderName());
        }
    };

    private View.OnClickListener backClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    private View.OnClickListener cameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            captureImageWithPermission();
        }
    };

    private View.OnClickListener doneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onDone();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        config = intent.getParcelableExtra(Config.EXTRA_CONFIG);
        if (config.isKeepScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        setContentView(R.layout.imagepicker_activity_picker);

        setupView();
        setupComponents();
        setupToolbar();

    }

    private void setupView() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        progressWheel = findViewById(R.id.progressWheel);
        emptyLayout = findViewById(R.id.layout_empty);
        snackBar = findViewById(R.id.snackbar);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(config.getStatusBarColor());
        }

        progressWheel.setBarColor(config.getProgressBarColor());
        findViewById(R.id.container).setBackgroundColor(config.getBackgroundColor());


    }

    private void setupComponents() {
        recyclerViewManager = new RecyclerViewManager(recyclerView, config, getResources().getConfiguration().orientation);
        recyclerViewManager.setupAdapters(imageClickListener, folderClickListener);
        recyclerViewManager.setOnImageSelectionListener(new OnImageSelectionListener() {
            @Override
            public void onSelectionUpdate(List<Image> images) {
                invalidateToolbar();
                if (!config.isMultipleMode() && !images.isEmpty()) {
                    onDone();
                }
            }
        });

        presenter = new ImagePickerPresenter(new ImageFileLoader(this));
        presenter.attachView(this);
    }

    private void setupToolbar() {
        toolbar.config(config);
        toolbar.setOnBackClickListener(backClickListener);
        toolbar.setOnCameraClickListener(cameraClickListener);
        toolbar.setOnDoneClickListener(doneClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataWithPermission();
    }


    private void setImageAdapter(List<Image> images, String title) {
        recyclerViewManager.setImageAdapter(images, title);
        invalidateToolbar();
    }

    private void setFolderAdapter(List<Folder> folders) {
        recyclerViewManager.setFolderAdapter(folders);
        invalidateToolbar();
    }

    private void invalidateToolbar() {
        toolbar.setTitle(recyclerViewManager.getTitle());
        toolbar.showDoneButton(recyclerViewManager.isShowDoneButton());

    }

    private void onDone() {
        presenter.onDoneSelectImages(recyclerViewManager.getSelectedImages());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recyclerViewManager.changeOrientation(newConfig.orientation);
    }


    private void getDataWithPermission() {

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        PermissionHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionHelper.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                PermissionHelper.requestAllPermissions(ImagePickerActivity.this, permissions, Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                PermissionHelper.requestAllPermissions(ImagePickerActivity.this, permissions, Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }

            @Override
            public void onPermissionDisabled() {
                snackBar.show(R.string.imagepicker_msg_no_write_external_storage_permission, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PermissionHelper.openAppSettings(ImagePickerActivity.this);
                    }
                });
            }

            @Override
            public void onPermissionGranted() {
                getData();
            }
        });
    }

    private void getData() {
        presenter.abortLoading();
        presenter.loadImages(config.isFolderMode());
    }


    private void captureImageWithPermission() {

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        PermissionHelper.checkPermission(this, Manifest.permission.CAMERA, new PermissionHelper.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                PermissionHelper.requestAllPermissions(ImagePickerActivity.this, permissions, Config.RC_CAMERA_PERMISSION);
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                PermissionHelper.requestAllPermissions(ImagePickerActivity.this, permissions, Config.RC_CAMERA_PERMISSION);
            }

            @Override
            public void onPermissionDisabled() {
                snackBar.show(R.string.imagepicker_msg_no_camera_permission, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PermissionHelper.openAppSettings(ImagePickerActivity.this);
                    }
                });
            }

            @Override
            public void onPermissionGranted() {
                captureImage();
            }
        });
    }


    private void captureImage() {
        if (!CameraHelper.checkCameraAvailability(this)) {
            return;
        }
        presenter.captureImage(this, config, Config.RC_CAPTURE_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.RC_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            presenter.finishCaptureImage(this, data, config);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION: {
                if (PermissionHelper.hasGranted(grantResults)) {
                    logger.d("Write External permission granted");
                    getData();
                    return;
                }
                logger.e("Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
                finish();
            }
            case Config.RC_CAMERA_PERMISSION: {
                if (PermissionHelper.hasGranted(grantResults)) {
                    logger.d("Camera permission granted");
                    captureImage();
                    return;
                }
                logger.e("Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
                break;
            }
            default: {
                logger.d("Got unexpected permission result: " + requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (handler == null) {
            handler = new Handler();
        }
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                getData();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (presenter != null) {
            presenter.abortLoading();
            presenter.detachView();
        }

        if (observer != null) {
            getContentResolver().unregisterContentObserver(observer);
            observer = null;
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public void onBackPressed() {
        recyclerViewManager.handleBack(new OnBackAction() {
            @Override
            public void onBackToFolder() {
                invalidateToolbar();
            }

            @Override
            public void onFinishImagePicker() {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * MVP view methods
     */

    @Override
    public void showLoading(boolean isLoading) {
        progressWheel.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    @Override
    public void showFetchCompleted(List<Image> images, List<Folder> folders) {
        if (config.isFolderMode()) {
            setFolderAdapter(folders);
        } else {
            setImageAdapter(images, config.getImageTitle());
        }
    }

    @Override
    public void showError(Throwable throwable) {
        String message = getString(R.string.imagepicker_error_unknown);
        if (throwable != null && throwable instanceof NullPointerException) {
            message = getString(R.string.imagepicker_error_images_not_exist);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmpty() {
        progressWheel.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCapturedImage(List<Image> images) {
        boolean shouldSelect = recyclerViewManager.selectImage();
        if (shouldSelect) {
            recyclerViewManager.addSelectedImages(images);
        }
        getDataWithPermission();
    }

    @Override
    public void finishPickImages(List<Image> images) {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, (ArrayList<? extends Parcelable>) images);
        setResult(RESULT_OK, data);
        finish();
    }
}
