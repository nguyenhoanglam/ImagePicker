/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.camera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.databinding.ImagepickerActivityCameraBinding
import com.nguyenhoanglam.imagepicker.helper.Constants
import com.nguyenhoanglam.imagepicker.helper.DeviceHelper
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper
import com.nguyenhoanglam.imagepicker.helper.ToastHelper
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ImagepickerActivityCameraBinding
    private lateinit var config: ImagePickerConfig

    private val cameraModule = CameraModule()
    private var alertDialog: AlertDialog? = null
    private var isOpeningCamera = false

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraModule.saveImage(this@CameraActivity, config, object : OnImageReadyListener {
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
            Constants.EXTRA_CONFIG, ImagePickerConfig::class.java
        )!!
        else intent.getParcelableExtra(Constants.EXTRA_CONFIG)!!
        config.initDefaultValues(this@CameraActivity)

        binding = ImagepickerActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isOpeningCamera && (alertDialog == null || !alertDialog!!.isShowing)) {
            captureImageWithPermission()
        }
    }

    private fun captureImageWithPermission() {
        val isCameraPermissionDeclared = PermissionHelper.isPermissionDeclared(
            this@CameraActivity, Manifest.permission.CAMERA
        )

        if (DeviceHelper.isMinSdk29) {
            if (isCameraPermissionDeclared) {
                val cameraPermission = Manifest.permission.CAMERA
                when (PermissionHelper.checkPermission(
                    this@CameraActivity, cameraPermission
                )) {
                    PermissionHelper.STATUS.GRANTED -> captureImage()

                    PermissionHelper.STATUS.NOT_GRANTED -> PermissionHelper.requestAllPermissions(
                        this@CameraActivity,
                        arrayOf(cameraPermission),
                        Constants.RC_CAMERA_PERMISSION
                    )

                    PermissionHelper.STATUS.DENIED -> PermissionHelper.requestAllPermissions(
                        this@CameraActivity,
                        arrayOf(cameraPermission),
                        Constants.RC_CAMERA_PERMISSION
                    )

                    else -> showOpenSettingDialog(resources.getString(R.string.imagepicker_msg_no_camera_permission))
                }
            } else {
                captureImage()
            }
        } else {
            if (isCameraPermissionDeclared) {
                val statuses = PermissionHelper.checkPermissions(
                    this@CameraActivity,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                )

                if (statuses[0] == PermissionHelper.STATUS.GRANTED && statuses[1] == PermissionHelper.STATUS.GRANTED) {
                    captureImage()
                } else if (statuses[0] == PermissionHelper.STATUS.DISABLED && statuses[1] == PermissionHelper.STATUS.DISABLED) {
                    resources.getString(R.string.imagepicker_msg_no_camera_permission)
                } else if (statuses[0] == PermissionHelper.STATUS.DISABLED) {
                    resources.getString(R.string.imagepicker_msg_no_camera_permission)
                } else if (statuses[1] == PermissionHelper.STATUS.DISABLED) {
                    resources.getString(R.string.imagepicker_msg_no_photo_access_permission)
                } else {
                    val requestPermissions = ArrayList<String>()
                    for ((index, value) in statuses.withIndex()) {
                        if (value == PermissionHelper.STATUS.NOT_GRANTED || value == PermissionHelper.STATUS.DENIED) {
                            requestPermissions.add(if (index == 0) Manifest.permission.CAMERA else Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }

                    PermissionHelper.requestAllPermissions(
                        this@CameraActivity,
                        requestPermissions.toTypedArray(),
                        Constants.RC_CAMERA_PERMISSION
                    )
                }
            } else {
                val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                when (PermissionHelper.checkPermission(
                    this,
                    writePermission,
                )) {
                    PermissionHelper.STATUS.GRANTED -> captureImage()

                    PermissionHelper.STATUS.NOT_GRANTED -> PermissionHelper.requestAllPermissions(
                        this@CameraActivity, arrayOf(writePermission), Constants.RC_WRITE_PERMISSION
                    )

                    PermissionHelper.STATUS.DENIED -> PermissionHelper.requestAllPermissions(
                        this@CameraActivity, arrayOf(writePermission), Constants.RC_WRITE_PERMISSION
                    )

                    else -> showOpenSettingDialog(resources.getString(R.string.imagepicker_msg_no_photo_access_permission))
                }
            }
        }
    }

    private fun showOpenSettingDialog(message: String) {
        val builder = AlertDialog.Builder(
            ContextThemeWrapper(
                this@CameraActivity, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog
            )
        )
        with(builder) {
            setMessage(message)
            setNegativeButton(R.string.imagepicker_action_cancel) { _, _ ->
                finish()
            }
            setPositiveButton(R.string.imagepicker_action_ok) { _, _ ->
                PermissionHelper.openAppSettings(this@CameraActivity)
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
            ToastHelper.show(this, getString(R.string.imagepicker_error_camera))
            return
        }

        resultLauncher.launch(intent)
        isOpeningCamera = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.RC_WRITE_PERMISSION, Constants.RC_CAMERA_PERMISSION -> {
                if (PermissionHelper.hasGranted(grantResults)) {
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

    @Suppress("DEPRECATION")
    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, 0, 0)
        } else {
            overridePendingTransition(0, 0)
        }
    }
}