package com.imagepicker.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.imagepicker.example.databinding.ActivityMainBinding
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.model.RootDirectory
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import java.util.*

class MainActivity : AppCompatActivity() {

    private var adapter: ImageAdapter? = null
    private var images = ArrayList<Image>()

    private lateinit var binding: ActivityMainBinding

    private val launcher = registerImagePicker { it ->
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

        binding.pickImageButton.setOnClickListener { start() }
        binding.launchFragmentButton.setOnClickListener { launchFragment() }
    }

    private fun start() {
        val folderMode = binding.folderModeSwitch.isChecked
        val multipleMode = binding.multipleModeSwitch.isChecked
        val cameraOnly = binding.cameraOnlySwitch.isChecked
        val showCamera = binding.showCameraSwitch.isChecked
        val showNumberIndicator = binding.showNumberIndicatorSwitch.isChecked
        val alwaysShowDone = binding.alwaysShowDoneSwitch.isChecked

        val config = ImagePickerConfig(
            statusBarColor = "#00796B",
            isLightStatusBar = false,
            toolbarColor = "#009688",
            toolbarTextColor = "#FFFFFF",
            toolbarIconColor = "#FFFFFF",
            backgroundColor = "#000000",
            progressIndicatorColor = "#009688",
            selectedIndicatorColor = "#2196F3",
            isCameraOnly = cameraOnly,
            isMultipleMode = multipleMode,
            isFolderMode = folderMode,
            doneTitle = "DONE",
            folderTitle = "Albums",
            imageTitle = "Photos",
            isShowCamera = showCamera,
            isShowNumberIndicator = showNumberIndicator,
            isAlwaysShowDoneButton = alwaysShowDone,
            rootDirectory = RootDirectory.DCIM,
            subDirectory = "Example",
            maxSize = 10,
            limitMessage = "You could only select up to 10 photos",
            selectedImages = images
        )

        launcher.launch(config)

    }

    private fun launchFragment() {
        val folderMode = binding.folderModeSwitch.isChecked
        val multipleMode = binding.multipleModeSwitch.isChecked
        val cameraOnly = binding.cameraOnlySwitch.isChecked
        val showCamera = binding.showCameraSwitch.isChecked
        val showNumberIndicator = binding.showNumberIndicatorSwitch.isChecked
        val alwaysShowDone = binding.alwaysShowDoneSwitch.isChecked

        val config = ImagePickerConfig(
            isFolderMode = folderMode,
            isMultipleMode = multipleMode,
            isCameraOnly = cameraOnly,
            isShowCamera = showCamera,
            isShowNumberIndicator = showNumberIndicator,
            isAlwaysShowDoneButton = alwaysShowDone,
            rootDirectory = RootDirectory.DOWNLOADS,
            subDirectory = "Photos",
            selectedImages = images,
            statusBarColor = "#000000",
            isLightStatusBar = false,
            backgroundColor = "#FFFFFF",
            folderGridCount = GridCount(2, 4),
            imageGridCount = GridCount(3, 5),
        )

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance(config))
            .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
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
}
