package com.nguyenhoanglam.imagepicker.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.helper.CameraHelper.checkCameraAvailability
import com.nguyenhoanglam.imagepicker.helper.LogHelper.Companion.instance
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.hasGranted
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.hasSelfPermission
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.hasSelfPermissions
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.openAppSettings
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.requestAllPermissions
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.shouldShowRequestPermissionRationale
import com.nguyenhoanglam.imagepicker.helper.PreferenceHelper.firstTimeAskingPermission
import com.nguyenhoanglam.imagepicker.helper.PreferenceHelper.isFirstTimeAskingPermission
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.widget.SnackBarView
import java.util.*

/**
 * Created by hoanglam on 8/21/17.
 */
class CameraActivty : AppCompatActivity(), CameraView {
    private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    private lateinit var snackBar: SnackBarView
    private lateinit var config: Config
    private lateinit var presenter: CameraPresenter
    private val logger = instance
    private var isOpeningCamera = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)
        setContentView(R.layout.imagepicker_activity_camera)
        snackBar = findViewById(R.id.snackbar)
        presenter = CameraPresenter()
        presenter.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        if (hasSelfPermissions(this, permissions) && isOpeningCamera) {
            isOpeningCamera = false
        } else if (!snackBar.isShowing) {
            captureImageWithPermission()
        }
    }

    private fun captureImageWithPermission() {
        if (hasSelfPermissions(this, permissions)) {
            captureImage()
        } else {
            logger?.w("Camera permission is not granted. Requesting permission")
            requestCameraPermission()
        }
    }

    private fun captureImage() {
        if (!checkCameraAvailability(this)) {
            finish()
            return
        }
        presenter.captureImage(this, config, Config.RC_CAPTURE_IMAGE)
        isOpeningCamera = true
    }

    private fun requestCameraPermission() {
        logger?.w("Write External permission is not granted. Requesting permission")
        var hasPermissionDisabled = false
        val wesGranted = hasSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val cameraGranted = hasSelfPermission(this, Manifest.permission.CAMERA)
        if (!wesGranted && !shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (!isFirstTimeAskingPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasPermissionDisabled = true
            }
        }
        if (!cameraGranted && !shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            if (!isFirstTimeAskingPermission(this, Manifest.permission.CAMERA)) {
                hasPermissionDisabled = true
            }
        }
        val permissions: MutableList<String> = ArrayList()
        if (!hasPermissionDisabled) {
            if (!wesGranted) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                firstTimeAskingPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, false)
            }
            if (!cameraGranted) {
                permissions.add(Manifest.permission.CAMERA)
                firstTimeAskingPermission(this, Manifest.permission.CAMERA, false)
            }
            requestAllPermissions(this, permissions.toTypedArray(), Config.RC_CAMERA_PERMISSION)
        } else {
            snackBar.show(R.string.imagepicker_msg_no_write_external_storage_camera_permission, View.OnClickListener { openAppSettings(this@CameraActivty) })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            Config.RC_CAMERA_PERMISSION -> {
                if (hasGranted(grantResults)) {
                    logger?.d("Camera permission granted")
                    captureImage()
                    return
                }
                logger?.e("Permission not granted: results len = " + grantResults.size +
                        " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")
                var shouldShowSnackBar = false
                for (grantResult in grantResults) {
                    if (hasGranted(grantResult)) {
                        shouldShowSnackBar = true
                        break
                    }
                }
                if (shouldShowSnackBar) {
                    snackBar.show(R.string.imagepicker_msg_no_write_external_storage_camera_permission, View.OnClickListener { openAppSettings(this@CameraActivty) })
                } else {
                    finish()
                }
            }
            else -> {
                logger?.d("Got unexpected permission result: $requestCode")
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.RC_CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.finishCaptureImage(this, config)
            } else {
                setResult(Activity.RESULT_CANCELED, Intent())
                finish()
            }
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter.detachView()
        }
    }

    override fun finishPickImages(images: List<Image>) {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, images as ArrayList<out Parcelable?>)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}