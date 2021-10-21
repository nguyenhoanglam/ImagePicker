/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.databinding.ImagepickerActivityImagepickerBinding
import com.nguyenhoanglam.imagepicker.helper.*
import com.nguyenhoanglam.imagepicker.listener.OnFolderClickListener
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.camera.CameraModule
import com.nguyenhoanglam.imagepicker.ui.camera.OnImageReadyListener

class ImagePickerActivity : AppCompatActivity(), OnFolderClickListener, OnImageSelectListener {

    private lateinit var binding: ImagepickerActivityImagepickerBinding

    private var config: Config? = null
    private lateinit var viewModel: ImagePickerViewModel
    private val cameraModule = CameraModule()

    private val backClickListener = View.OnClickListener { onBackPressed() }
    private val cameraClickListener = View.OnClickListener { captureImageWithPermission() }
    private val doneClickListener = View.OnClickListener { onDone() }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraModule.saveImage(
                    this@ImagePickerActivity,
                    config!!,
                    object : OnImageReadyListener {
                        override fun onImageReady(images: ArrayList<Image>) {
                            fetchDataWithPermission()
                        }

                        override fun onImageNotReady() {
                            fetchDataWithPermission()
                        }
                    })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent == null) {
            finish()
            return
        }

        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)

        // Setup status bar theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = config!!.getStatusBarColor()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
                config!!.isLightStatusBar
        }

        binding = ImagepickerActivityImagepickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ImagePickerViewModelFactory(this.application)).get(
            ImagePickerViewModel::class.java
        )
        viewModel.setConfig(config!!)
        viewModel.selectedImages.observe(this, {
            binding.toolbar.showDoneButton(config!!.isAlwaysShowDoneButton || it.isNotEmpty())
        })

        setupViews()
    }

    override fun onResume() {
        super.onResume()
        fetchDataWithPermission()
    }

    private fun setupViews() {
        binding.toolbar.config(config!!)
        binding.toolbar.setOnBackClickListener(backClickListener)
        binding.toolbar.setOnCameraClickListener(cameraClickListener)
        binding.toolbar.setOnDoneClickListener(doneClickListener)

        val initialFragment =
            if (config!!.isFolderMode) FolderFragment.newInstance(config!!.folderGridCount)
            else ImageFragment.newInstance(config!!.imageGridCount)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, initialFragment)
            .commit()
    }


    private fun fetchDataWithPermission() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        PermissionHelper.checkPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            object : PermissionHelper.PermissionAskListener {
                override fun onNeedPermission() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_READ_EXTERNAL_STORAGE_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_READ_EXTERNAL_STORAGE_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    binding.snackbar.show(
                        R.string.imagepicker_msg_no_external_storage_permission
                    ) {
                        PermissionHelper.openAppSettings(this@ImagePickerActivity)
                    }
                }

                override fun onPermissionGranted() {
                    fetchData()
                }
            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Config.RC_READ_EXTERNAL_STORAGE_PERMISSION -> {
                if (PermissionHelper.hasGranted(grantResults)) {
                    fetchData()
                } else {
                    finish()
                }
            }
            Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                if (PermissionHelper.hasGranted(grantResults)) {
                    captureImage()
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun fetchData() {
        viewModel.fetchImages()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment != null && fragment is FolderFragment) {
            binding.toolbar.setTitle(config!!.folderTitle)
        }
    }

    private fun onDone() {
        val selectedImages = viewModel.selectedImages.value
        finishPickImages(selectedImages ?: arrayListOf())
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
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                    )
                }

                override fun onPermissionPreviouslyDenied() {
                    PermissionHelper.requestAllPermissions(
                        this@ImagePickerActivity,
                        permissions,
                        Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION
                    )
                }

                override fun onPermissionDisabled() {
                    binding.snackbar.show(
                        R.string.imagepicker_msg_no_external_storage_permission
                    ) {
                        PermissionHelper.openAppSettings(this@ImagePickerActivity)
                    }
                }

                override fun onPermissionGranted() {
                    captureImage()
                }
            })
    }


    fun captureImage() {
        if (!DeviceHelper.checkCameraAvailability(this)) {
            return
        }

        val intent = cameraModule.getCameraIntent(this@ImagePickerActivity, config!!)
        if (intent == null) {
            ToastHelper.show(this, getString(R.string.imagepicker_error_open_camera))
            return
        }

        resultLauncher.launch(intent)
    }

    private fun finishPickImages(images: ArrayList<Image>) {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, images)
        setResult(Activity.RESULT_OK, data)
        finish()
    }


    override fun onFolderClick(folder: Folder) {
        supportFragmentManager.beginTransaction()
            .add(
                R.id.fragmentContainer,
                ImageFragment.newInstance(folder.bucketId, config!!.imageGridCount)
            )
            .addToBackStack(null)
            .commit()
        binding.toolbar.setTitle(folder.name)
    }

    override fun onSelectedImagesChanged(selectedImages: ArrayList<Image>) {
        viewModel.selectedImages.value = selectedImages
    }

    override fun onSingleModeImageSelected(image: Image) {
        finishPickImages(ImageHelper.singleListFromImage(image))
    }
}