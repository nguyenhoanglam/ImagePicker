package com.nguyenhoanglam.imagepicker.ui.camera;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.nguyenhoanglam.imagepicker.R;
import com.nguyenhoanglam.imagepicker.helper.CameraHelper;
import com.nguyenhoanglam.imagepicker.helper.LogHelper;
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper;
import com.nguyenhoanglam.imagepicker.helper.PreferenceHelper;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.widget.SnackBarView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoanglam on 8/21/17.
 */

public class CameraActivty extends AppCompatActivity implements CameraView {

    private final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private SnackBarView snackBar;

    private Config config;
    private CameraPresenter presenter;
    private LogHelper logger = LogHelper.getInstance();
    private boolean isOpeningCamera = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        setContentView(R.layout.imagepicker_activity_camera);
        snackBar = findViewById(R.id.snackbar);

        presenter = new CameraPresenter();
        presenter.attachView(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionHelper.hasSelfPermissions(this, permissions) && isOpeningCamera) {
            isOpeningCamera = false;
        } else if (!snackBar.isShowing()) {
            captureImageWithPermission();
        }
    }

    private void captureImageWithPermission() {
        if (PermissionHelper.hasSelfPermissions(this, permissions)) {
            captureImage();
        } else {
            logger.w("Camera permission is not granted. Requesting permission");
            requestCameraPermission();
        }
    }

    private void captureImage() {
        if (!CameraHelper.checkCameraAvailability(this)) {
            finish();
            return;
        }

        presenter.captureImage(this, config, Config.RC_CAPTURE_IMAGE);
        isOpeningCamera = true;

    }

    private void requestCameraPermission() {
        logger.w("Write External permission is not granted. Requesting permission");

        boolean hasPermissionDisbled = false;

        boolean wesGranted = PermissionHelper.hasSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean cameraGranted = PermissionHelper.hasSelfPermission(this, Manifest.permission.CAMERA);

        if (!wesGranted && !PermissionHelper.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (!PreferenceHelper.isFirstTimeAskingPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasPermissionDisbled = true;
            }
        }

        if (!cameraGranted && !PermissionHelper.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            if (!PreferenceHelper.isFirstTimeAskingPermission(this, Manifest.permission.CAMERA)) {
                hasPermissionDisbled = true;
            }
        }

        List<String> permissions = new ArrayList<>();

        if (!hasPermissionDisbled) {

            if (!wesGranted) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                PreferenceHelper.firstTimeAskingPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
            }

            if (!cameraGranted) {
                permissions.add(Manifest.permission.CAMERA);
                PreferenceHelper.firstTimeAskingPermission(this, Manifest.permission.CAMERA, false);
            }

            PermissionHelper.requestAllPermissions(this, permissions.toArray(new String[permissions.size()]), Config.RC_CAMERA_PERMISSION);

        } else {
            snackBar.show(R.string.imagepicker_msg_no_write_external_storage_camera_permission, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PermissionHelper.openAppSettings(CameraActivty.this);
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case Config.RC_CAMERA_PERMISSION: {
                if (PermissionHelper.hasGranted(grantResults)) {
                    logger.d("Camera permission granted");
                    captureImage();
                    return;
                }

                logger.e("Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

                boolean shouldShowSnackBar = false;
                for (int grantResult : grantResults) {
                    if (PermissionHelper.hasGranted(grantResult)) {
                        shouldShowSnackBar = true;
                        break;
                    }
                }

                if (shouldShowSnackBar) {
                    snackBar.show(R.string.imagepicker_msg_no_write_external_storage_camera_permission, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PermissionHelper.openAppSettings(CameraActivty.this);
                        }
                    });
                } else {
                    finish();
                }
                break;
            }
            default: {
                logger.d("Got unexpected permission result: " + requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                finish();
                break;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            presenter.finishCaptureImage(this, data, config);
        } else {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
        }
    }

    @Override
    public void finishPickImages(List<Image> images) {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, (ArrayList<? extends Parcelable>) images);
        setResult(RESULT_OK, data);
        finish();
    }
}
