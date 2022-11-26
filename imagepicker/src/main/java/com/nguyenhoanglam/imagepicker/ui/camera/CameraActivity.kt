/*
 * Copyright (C) 2021 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.camera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.databinding.ImagepickerActivityCameraBinding
import com.nguyenhoanglam.imagepicker.helper.Constants
import com.nguyenhoanglam.imagepicker.helper.DeviceHelper
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.hasGranted
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.openAppSettings
import com.nguyenhoanglam.imagepicker.helper.ToastHelper
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ImagepickerActivityCameraBinding
    private lateinit var config: ImagePickerConfig

    private val cameraModule = CameraModule()
    private var alertDialog: AlertDialog? = null
    private var isOpeningCamera = false

    private val permissions =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraModule.saveImage(
                    this@CameraActivity,
                    config,
                    object : OnImageReadyListener {
                        override fun onImageReady(images: ArrayList<Image>) {
                            finishCaptureImage(images)
                        }

                        override fun onImageNotReady() {
                            finishCaptureImage(arrayListOf())
                        }
                    })
            } else {
                finishCaptureImage(arrayListOf())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent == null) {
            finish()
            return
        }

        @Suppress("DEPRECATION")
        config = if (DeviceHelper.isMinSdk33) intent.getParcelableExtra(
            Constants.EXTRA_CONFIG,
            ImagePickerConfig::class.java
        )!! else intent.getParcelableExtra(Constants.EXTRA_CONFIG)!!
        config.initDefaultValues(this@CameraActivity)

        binding = ImagepickerActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (!isOpeningCamera && (alertDialog == null || !alertDialog!!.isShowing)) {
            captureImageWithPermission()
        }
    }

    private fun captureImageWithPermission() {
        if (DeviceHelper.isMinSdk29) {
            captureImage()
            return
        }

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionHelper.checkPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            object : PermissionHelper.PermissionAskListener {
                override fun onNeedPermission() {
                    PermissionHelper.requestAllPermissions(
                        this@CameraActivity,
                        permissions,
                        Constants.RC_WRITE_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    PermissionHelper.requestAllPermissions(
                        this@CameraActivity,
                        permissions,
                        Constants.RC_WRITE_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    showOpenSettingDialog()
                }

                override fun onPermissionGranted() {
                    captureImage()
                }
            })
    }

    private fun showOpenSettingDialog() {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                this@CameraActivity,
                R.style.Theme_AppCompat_Light_Dialog
            )
        )
        with(builder) {
            setMessage(R.string.imagepicker_msg_no_external_storage_permission)
            setNegativeButton(R.string.imagepicker_action_cancel) { _, _ ->
                finish()
            }
            setPositiveButton(R.string.imagepicker_action_ok) { _, _ ->
                openAppSettings(this@CameraActivity)
                finish()
            }
        }

        alertDialog = builder.create()
        alertDialog!!.show()
    }

    private fun captureImage() {
        if (!DeviceHelper.checkCameraAvailability(this)) {
            finish()
            return
        }

        val intent = cameraModule.getCameraIntent(this@CameraActivity, config)
        if (intent == null) {
            ToastHelper.show(this, getString(R.string.imagepicker_error_open_camera))
            return
        }

        resultLauncher.launch(intent)
        isOpeningCamera = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.RC_WRITE_PERMISSION -> {
                if (hasGranted(grantResults)) {
                    captureImage()
                } else {
                    finish()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                finish()
            }
        }
    }

    private fun finishCaptureImage(images: ArrayList<Image>) {
        val data = Intent()
        data.putParcelableArrayListExtra(Constants.EXTRA_IMAGES, images)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}