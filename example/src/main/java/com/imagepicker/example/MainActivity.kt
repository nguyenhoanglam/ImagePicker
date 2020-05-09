package com.imagepicker.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var adapter: ImageAdapter? = null
    private var images = ArrayList<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adapter = ImageAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        pickImageButton.setOnClickListener { start() }
        launchFragmentButton.setOnClickListener { launchFragment() }
    }

    private fun start() {
        val folderMode = folderModeSwitch.isChecked
        val multipleMode = multipleModeSwitch.isChecked
        val cameraOnly = cameraOnlySwitch.isChecked
        val showCamera = showCameraSwitch.isChecked
        val showNumberIndicator = showNumberIndicatorSwitch.isChecked
        val alwaysShowDone = alwaysShowDoneSwitch.isChecked

        ImagePicker.with(this)
            .setFolderMode(folderMode)
            .setMultipleMode(multipleMode)
            .setCameraOnly(cameraOnly)
            .setShowCamera(showCamera)
            .setShowNumberIndicator(showNumberIndicator)
            .setAlwaysShowDoneButton(alwaysShowDone)
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName("Image Picker")
            .setSelectedImages(images)
            .setRequestCode(100)
            .start()
    }

    private fun launchFragment() {
        val folderMode = folderModeSwitch.isChecked
        val multipleMode = multipleModeSwitch.isChecked
        val cameraOnly = cameraOnlySwitch.isChecked
        val showCamera = showCameraSwitch.isChecked
        val showNumberIndicator = showNumberIndicatorSwitch.isChecked
        val alwaysShowDone = alwaysShowDoneSwitch.isChecked

        val config = ImagePicker.with(this)
            .setFolderMode(folderMode)
            .setMultipleMode(multipleMode)
            .setCameraOnly(cameraOnly)
            .setShowCamera(showCamera)
            .setShowNumberIndicator(showNumberIndicator)
            .setAlwaysShowDoneButton(alwaysShowDone)
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName("Image Picker")
            .setSelectedImages(images)
            .setRequestCode(100).config

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MainFragment.newInstance(config))
            .commitAllowingStateLoss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            adapter!!.setData(images)
        }
        super.onActivityResult(requestCode, resultCode, data)
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
