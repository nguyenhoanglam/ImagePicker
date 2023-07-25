/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.imagepicker.example

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.imagepicker.example.databinding.ActivityMainBinding
import com.nguyenhoanglam.imagepicker.model.CustomColor
import com.nguyenhoanglam.imagepicker.model.CustomDrawable
import com.nguyenhoanglam.imagepicker.model.CustomMessage
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.IndicatorType
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker

class MainActivity : AppCompatActivity() {

    private var adapter: ImageAdapter? = null
    private var images = ArrayList<Image>()

    private lateinit var binding: ActivityMainBinding

    private val launcher = registerImagePicker {
        images = it
        adapter!!.setData(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ImageAdapter(this)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

        binding.launchPickerButton.setOnClickListener { start() }
        binding.launchFragmentButton.setOnClickListener { launchFragment() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm = supportFragmentManager
                val fragment = fm.findFragmentById(R.id.fragment_container)
                if (fragment == null) {
                    finish()
                } else {
                    fm.beginTransaction()
                        .remove(fragment)
                        .commitAllowingStateLoss()
                }
            }
        })
    }

    private fun start() {
        val folderMode = binding.folderModeSwitch.isChecked
        val multiSelectMode = binding.multiSelectModeSwitch.isChecked
        val cameraMode = binding.cameraModeSwitch.isChecked
        val showCamera = binding.showCameraSwitch.isChecked
        val selectAllEnabled = binding.showSelectAllSwitch.isChecked
        val unselectAllEnabled = binding.showUnselectAllSwitch.isChecked
        val showNumberIndicator = binding.showNumberIndicatorSwitch.isChecked
        val enableImageTransition = binding.enableImageTransitionSwitch.isChecked

        val config = ImagePickerConfig(
            isCameraMode = cameraMode,
            isMultiSelectMode = multiSelectMode,
            isFolderMode = folderMode,
            isShowCamera = showCamera,
            isSelectAllEnabled = selectAllEnabled,
            isUnselectAllEnabled = unselectAllEnabled,
            isImageTransitionEnabled = enableImageTransition,
            selectedIndicatorType = if (showNumberIndicator) IndicatorType.NUMBER else IndicatorType.CHECK_MARK,
            limitSize = 100,
            rootDirectory = RootDirectory.DCIM,
            subDirectory = "Image Picker",
            folderGridCount = GridCount(2, 4),
            imageGridCount = GridCount(3, 5),
            selectedImages = images,
            customColor = CustomColor(
                background = "#000000",
                statusBar = "#000000",
                toolbar = "#212121",
                toolbarTitle = "#FFFFFF",
                toolbarIcon = "#FFFFFF",
                doneButtonTitle = "#FFFFFF",
                snackBarBackground = "#323232",
                snackBarMessage = "#FFFFFF",
                snackBarButtonTitle = "#4CAF50",
                loadingIndicator = "#757575",
                selectedImageIndicator = "#1976D2"
            ),
            customMessage = CustomMessage(
                reachLimitSize = "You can only select up to 10 images.",
                cameraError = "Unable to open camera.",
                noCamera = "Your device has no camera.",
                noImage = "No image found.",
                noPhotoAccessPermission = "Please allow permission to access photos and media.",
                noCameraPermission = "Please allow permission to access camera."
            ),
            customDrawable = CustomDrawable(
                backIcon = R.drawable.ic_back,
                cameraIcon = R.drawable.ic_camera,
                selectAllIcon = R.drawable.ic_select_all,
                unselectAllIcon = R.drawable.ic_unselect_all,
                loadingImagePlaceholder = R.drawable.img_loading_placeholder
            )
        )

        launcher.launch(config)
    }

    private fun launchFragment() {
        val folderMode = binding.folderModeSwitch.isChecked
        val multiSelectMode = binding.multiSelectModeSwitch.isChecked
        val cameraMode = binding.cameraModeSwitch.isChecked
        val showCamera = binding.showCameraSwitch.isChecked
        val selectAllEnabled = binding.showSelectAllSwitch.isChecked
        val unselectAllEnabled = binding.showUnselectAllSwitch.isChecked
        val showNumberIndicator = binding.showNumberIndicatorSwitch.isChecked
        val enableImageTransition = binding.enableImageTransitionSwitch.isChecked

        val config = ImagePickerConfig(
            isCameraMode = cameraMode,
            isMultiSelectMode = multiSelectMode,
            isFolderMode = folderMode,
            isShowCamera = showCamera,
            isSelectAllEnabled = selectAllEnabled,
            isUnselectAllEnabled = unselectAllEnabled,
            isImageTransitionEnabled = enableImageTransition,
            selectedIndicatorType = if (showNumberIndicator) IndicatorType.NUMBER else IndicatorType.CHECK_MARK,
            rootDirectory = RootDirectory.DOWNLOADS,
            subDirectory = "Photos"
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance(config))
            .commitAllowingStateLoss()
    }
}
