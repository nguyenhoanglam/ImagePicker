/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.camera.CameraActivity
import java.util.*

class ImagePicker(builder: Builder) {

    private var config: Config = builder.config

    internal class ActivityBuilder(
        private val activity: Activity,
        private val resultLauncher: ActivityResultLauncher<Intent>
    ) : Builder(activity) {
        override fun start() {
            if (config.isCameraOnly) {
                activity.overridePendingTransition(0, 0)
            }

            resultLauncher.launch(intent)
        }

        override val intent: Intent
            get() {
                val intent: Intent
                if (!config.isCameraOnly) {
                    intent = Intent(activity, ImagePickerActivity::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                } else {
                    intent = Intent(activity, CameraActivity::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
                return intent
            }

    }

    internal class FragmentBuilder(
        private val fragment: Fragment,
        private val resultLauncher: ActivityResultLauncher<Intent>
    ) : Builder(fragment) {
        override fun start() {
            val intent = intent
            if (config.isCameraOnly) {
                fragment.activity?.overridePendingTransition(0, 0)
            }
            resultLauncher.launch(intent)
        }

        override val intent: Intent
            get() {
                val intent: Intent
                if (!config.isCameraOnly) {
                    intent = Intent(fragment.activity, ImagePickerActivity::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                } else {
                    intent = Intent(fragment.activity, CameraActivity::class.java)
                    intent.putExtra(Config.EXTRA_CONFIG, config)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
                return intent
            }

    }

    abstract class Builder : BaseBuilder {

        abstract fun start()
        abstract val intent: Intent

        constructor(activity: Activity?) : super(
            activity
        )

        constructor(fragment: Fragment) : super(
            fragment.context
        )

        fun setToolbarColor(toolbarColor: String): Builder {
            config.setToolbarColor(toolbarColor)
            return this
        }

        fun setStatusBarColor(statusBarColor: String): Builder {
            config.setStatusBarColor(statusBarColor)
            return this
        }

        fun setIsLightStatusBar(isLightStatusBar: Boolean): Builder {
            config.isLightStatusBar = isLightStatusBar
            return this
        }

        fun setToolbarTextColor(toolbarTextColor: String): Builder {
            config.setToolbarTextColor(toolbarTextColor)
            return this
        }

        fun setToolbarIconColor(toolbarIconColor: String): Builder {
            config.setToolbarIconColor(toolbarIconColor)
            return this
        }

        fun setProgressBarColor(progressBarColor: String): Builder {
            config.setProgressBarColor(progressBarColor)
            return this
        }

        fun setBackgroundColor(backgroundColor: String): Builder {
            config.setBackgroundColor(backgroundColor)
            return this
        }

        fun setIndicatorColor(indicatorColor: String): Builder {
            config.setIndicatorColor(indicatorColor)
            return this
        }

        fun setCameraOnly(isCameraOnly: Boolean): Builder {
            config.isCameraOnly = isCameraOnly
            return this
        }

        fun setMultipleMode(isMultipleMode: Boolean): Builder {
            config.isMultipleMode = isMultipleMode
            return this
        }

        fun setFolderMode(isFolderMode: Boolean): Builder {
            config.isFolderMode = isFolderMode
            return this
        }

        fun setShowNumberIndicator(isShowNumberIndicator: Boolean): Builder {
            config.isShowNumberIndicator = isShowNumberIndicator
            return this
        }

        fun setShowCamera(isShowCamera: Boolean): Builder {
            config.isShowCamera = isShowCamera
            return this
        }

        fun setMaxSize(maxSize: Int): Builder {
            config.maxSize = maxSize
            return this
        }

        fun setFolderGridCount(portraitCount: Int, landscapeCount: Int): Builder {
            config.folderGridCount = GridCount(portraitCount, landscapeCount)
            return this
        }

        fun setImageGridCount(portraitCount: Int, landscapeCount: Int): Builder {
            config.imageGridCount = GridCount(portraitCount, landscapeCount)
            return this
        }

        fun setDoneTitle(doneTitle: String): Builder {
            config.doneTitle = doneTitle
            return this
        }

        fun setFolderTitle(folderTitle: String): Builder {
            config.folderTitle = folderTitle
            return this
        }

        fun setImageTitle(imageTitle: String): Builder {
            config.imageTitle = imageTitle
            return this
        }

        fun setLimitMessage(message: String): Builder {
            config.limitMessage = message
            return this
        }

        fun setRootDirectory(rootDirectory: String): Builder {
            config.rootDirectory = rootDirectory
            return this
        }

        fun setSubDirectory(subDirectory: String): Builder {
            config.subDirectory = subDirectory
            return this
        }

        fun setAlwaysShowDoneButton(isAlwaysShowDoneButton: Boolean): Builder {
            config.isAlwaysShowDoneButton = isAlwaysShowDoneButton
            return this
        }

        fun setSelectedImages(selectedImages: ArrayList<Image>?): Builder {
            config.selectedImages = selectedImages ?: arrayListOf()
            return this
        }
    }

    abstract class BaseBuilder(context: Context?) {

        var config: Config = Config()

        private fun getDefaultSubDirectoryName(context: Context): String {
            val pm = context.packageManager
            val ai: ApplicationInfo? = try {
                pm.getApplicationInfo(context.applicationContext.packageName ?: "", 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            return (if (ai != null) pm.getApplicationLabel(ai) else "Camera") as String
        }

        init {
            val resources = context!!.resources
            config.setToolbarColor("#212121")
            config.setStatusBarColor("#000000")
            config.isLightStatusBar = false
            config.setToolbarTextColor("#FFFFFF")
            config.setToolbarIconColor("#FFFFFF")
            config.setProgressBarColor("#4CAF50")
            config.setBackgroundColor("#424242")
            config.setIndicatorColor("#1976D2")
            config.isCameraOnly = false
            config.isMultipleMode = true
            config.isFolderMode = true
            config.isShowNumberIndicator = false
            config.isShowCamera = true
            config.maxSize = Config.MAX_SIZE
            config.folderGridCount = GridCount(2, 4)
            config.imageGridCount = GridCount(3, 5)
            config.doneTitle = resources.getString(R.string.imagepicker_action_done)
            config.folderTitle = resources.getString(R.string.imagepicker_title_folder)
            config.imageTitle = resources.getString(R.string.imagepicker_title_image)
            config.rootDirectory = Config.ROOT_DIR_DCIM
            config.subDirectory = getDefaultSubDirectoryName(context)
            config.isAlwaysShowDoneButton = false
            config.selectedImages = arrayListOf()
        }
    }

    companion object {
        @JvmStatic
        fun with(activity: Activity, resultLauncher: ActivityResultLauncher<Intent>): Builder {
            return ActivityBuilder(activity, resultLauncher)
        }

        @JvmStatic
        fun with(fragment: Fragment, resultLauncher: ActivityResultLauncher<Intent>): Builder {
            return FragmentBuilder(fragment, resultLauncher)
        }

        @JvmStatic
        fun getImages(data: Intent?): ArrayList<Image> {
            return if (data != null) data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)!!
            else arrayListOf()
        }
    }

}