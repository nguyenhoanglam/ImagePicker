/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.nguyenhoanglam.imagepicker.R
import kotlinx.parcelize.Parcelize

enum class StatusBarContent {
    LIGHT, DARK
}

enum class RootDirectory(val value: String) {
    DCIM(Environment.DIRECTORY_DCIM), PICTURES(Environment.DIRECTORY_PICTURES), DOWNLOADS(
        Environment.DIRECTORY_DOWNLOADS
    )
}

enum class IndicatorType {
    NUMBER, CHECK_MARK
}

fun Context.getHexColorFromResId(resId: Int): String {
    return "#${Integer.toHexString(ContextCompat.getColor(this, resId))}"
}

@Parcelize
class ImagePickerConfig(
    var isCameraMode: Boolean = false,
    var isSingleSelectMode: Boolean = false,
    var isFolderMode: Boolean = true,
    var isShowCamera: Boolean = true,
    var isAlwaysShowDoneButton: Boolean = true,
    var isSelectAllEnabled: Boolean = true,
    var isUnselectAllEnabled: Boolean = true,
    var isImageTransitionEnabled: Boolean = true,
    var statusBarContentMode: StatusBarContent = StatusBarContent.LIGHT,
    var selectedIndicatorType: IndicatorType = IndicatorType.NUMBER,
    var limitSize: Int = Int.MAX_VALUE,
    var folderGridCount: GridCount = GridCount(2, 4),
    var imageGridCount: GridCount = GridCount(3, 5),
    var imageSort: ImageSort = ImageSort(by = SortBy.DATE_ADDED, order = SortOrder.DESC),
    var doneButtonTitle: String? = null,
    var snackBarButtonTitle: String? = null,
    var folderTitle: String? = null,
    var imageTitle: String? = null,
    var rootDirectory: RootDirectory = RootDirectory.DCIM,
    var subDirectory: String? = null,
    var selectedImages: ArrayList<Image> = arrayListOf(),
    var customColor: CustomColor? = null,
    var customMessage: CustomMessage? = null,
    var customDrawable: CustomDrawable? = null
) : Parcelable {

    fun initDefaultValues(context: Context) {
        val resource = context.resources
        if (folderTitle == null) {
            folderTitle = resource.getString(R.string.imagepicker_title_folder)
        }

        if (imageTitle == null) {
            imageTitle = resource.getString(R.string.imagepicker_title_image)
        }

        if (doneButtonTitle == null) {
            doneButtonTitle = resource.getString(R.string.imagepicker_action_done)
        }

        if (snackBarButtonTitle == null) {
            snackBarButtonTitle = resource.getString(R.string.imagepicker_action_ok)
        }

        if (subDirectory == null) {
            subDirectory = getDefaultSubDirectoryName(context)
        }

        if (customColor == null) {
            customColor = CustomColor(
                background = context.getHexColorFromResId(R.color.imagepicker_background),
                statusBar = context.getHexColorFromResId(R.color.imagepicker_status_bar),
                toolbar = context.getHexColorFromResId(R.color.imagepicker_toolbar),
                toolbarTitle = context.getHexColorFromResId(R.color.imagepicker_toolbar_title),
                toolbarIcon = context.getHexColorFromResId(R.color.imagepicker_toolbar_icon),
                doneButtonTitle = context.getHexColorFromResId(R.color.imagepicker_done_button_title),
                snackBarBackground = context.getHexColorFromResId(R.color.imagepicker_snack_bar_background),
                snackBarMessage = context.getHexColorFromResId(R.color.imagepicker_snack_bar_message),
                snackBarButtonTitle = context.getHexColorFromResId(R.color.imagepicker_snack_bar_button_title),
                loadingIndicator = context.getHexColorFromResId(R.color.imagepicker_loading_indicator),
                selectedImageIndicator = context.getHexColorFromResId(R.color.imagepicker_selected_image_indicator),
            )
        } else {
            if (customColor!!.background == null) {
                customColor!!.background =
                    context.getHexColorFromResId(R.color.imagepicker_background)
            }

            if (customColor!!.statusBar == null) {
                customColor!!.statusBar =
                    context.getHexColorFromResId(R.color.imagepicker_status_bar)
            }

            if (customColor!!.toolbar == null) {
                customColor!!.toolbar = context.getHexColorFromResId(R.color.imagepicker_toolbar)
            }

            if (customColor!!.toolbarTitle == null) {
                customColor!!.toolbarTitle =
                    context.getHexColorFromResId(R.color.imagepicker_toolbar_title)
            }

            if (customColor!!.toolbarIcon == null) {
                customColor!!.toolbarIcon =
                    context.getHexColorFromResId(R.color.imagepicker_toolbar_icon)
            }

            if (customColor!!.doneButtonTitle == null) {
                customColor!!.doneButtonTitle =
                    context.getHexColorFromResId(R.color.imagepicker_done_button_title)
            }

            if (customColor!!.snackBarBackground == null) {
                customColor!!.snackBarBackground =
                    context.getHexColorFromResId(R.color.imagepicker_snack_bar_background)
            }

            if (customColor!!.snackBarMessage == null) {
                customColor!!.snackBarMessage =
                    context.getHexColorFromResId(R.color.imagepicker_snack_bar_message)
            }

            if (customColor!!.snackBarButtonTitle == null) {
                customColor!!.snackBarButtonTitle =
                    context.getHexColorFromResId(R.color.imagepicker_snack_bar_button_title)
            }

            if (customColor!!.loadingIndicator == null) {
                customColor!!.loadingIndicator =
                    context.getHexColorFromResId(R.color.imagepicker_loading_indicator)
            }

            if (customColor!!.selectedImageIndicator == null) {
                customColor!!.selectedImageIndicator =
                    context.getHexColorFromResId(R.color.imagepicker_selected_image_indicator)
            }
        }

        if (customMessage == null) {
            customMessage = CustomMessage(
                reachLimitSize = resource.getString(R.string.imagepicker_msg_selection_limit),
                cameraError = resource.getString(R.string.imagepicker_error_camera),
                noCamera = resource.getString(R.string.imagepicker_error_no_camera),
                noImage = resource.getString(R.string.imagepicker_msg_no_image),
                noPhotoAccessPermission = resource.getString(R.string.imagepicker_msg_no_photo_access_permission),
                noCameraPermission = resource.getString(R.string.imagepicker_msg_no_camera_permission),
            )
        } else {
            if (customMessage!!.cameraError == null) {
                customMessage!!.cameraError = resource.getString(R.string.imagepicker_error_camera)
            }

            if (customMessage!!.noCamera == null) {
                customMessage!!.noCamera = resource.getString(R.string.imagepicker_error_no_camera)
            }

            if (customMessage!!.noImage == null) {
                customMessage!!.noImage = resource.getString(R.string.imagepicker_msg_no_image)
            }

            if (customMessage!!.noPhotoAccessPermission == null) {
                customMessage!!.noPhotoAccessPermission =
                    resource.getString(R.string.imagepicker_msg_no_photo_access_permission)
            }

            if (customMessage!!.noCameraPermission == null) {
                customMessage!!.noCameraPermission =
                    resource.getString(R.string.imagepicker_msg_no_camera_permission)
            }
        }

        if (customDrawable == null) {
            customDrawable = CustomDrawable()
        }
    }

    private fun getDefaultSubDirectoryName(context: Context): String {
        val pm = context.packageManager
        val ai: ApplicationInfo? = try {
            pm.getApplicationInfo(context.applicationContext.packageName ?: "", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return (if (ai != null) pm.getApplicationLabel(ai) else "Camera") as String
    }
}