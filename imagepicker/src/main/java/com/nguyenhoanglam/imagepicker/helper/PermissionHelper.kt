/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.helper

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.app.ActivityCompat

object PermissionHelper {

    enum class STATUS {
        GRANTED,
        NOT_GRANTED,
        DENIED,
        DISABLED
    }

    private const val PREFERENCE_FILE_NAME = "ImagePicker"

    fun checkPermission(activity: Activity, permission: String): STATUS {
        if (!hasSelfPermission(activity, permission)) {
            if (shouldShowRequestPermissionRationale(activity, permission)) return STATUS.DENIED

            if (!isInitialRequestCalled(activity, permission)) {
                setInitialRequestCalled(activity, permission)
                return STATUS.NOT_GRANTED
            }

            return STATUS.DISABLED
        }

        return STATUS.GRANTED
    }

    fun checkPermissions(activity: Activity, permissions: Array<String>): Array<STATUS> {
        val statuses = ArrayList<STATUS>()
        for (permission in permissions) {
            statuses.add(checkPermission(activity, permission))
        }

        return statuses.toTypedArray()
    }

    fun openAppSettings(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        activity.startActivity(intent)
    }

    private fun hasGranted(grantResult: Int): Boolean {
        return grantResult == PackageManager.PERMISSION_GRANTED
    }

    fun hasGranted(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (!hasGranted(result)) {
                return false
            }
        }

        return true
    }

    private fun hasSelfPermission(context: Context, permission: String): Boolean {
        if (shouldAskPermission()) return permissionHasGranted(context, permission)

        return true
    }

    fun hasSelfPermissions(context: Context, permissions: Array<String>): Boolean {
        if (shouldAskPermission()) {
            for (permission in permissions) {
                if (!permissionHasGranted(context, permission)) {
                    return false
                }
            }
        }

        return true
    }

    fun requestAllPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        if (shouldAskPermission()) {
            internalRequestPermissions(activity, permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun internalRequestPermissions(
        activity: Activity?,
        permissions: Array<String>,
        requestCode: Int
    ) {
        requireNotNull(activity) { "Given activity is null." }
        activity.requestPermissions(permissions, requestCode)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun permissionHasGranted(context: Context, permission: String): Boolean {
        return hasGranted(context.checkSelfPermission(permission))
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
    private fun shouldAskPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun shouldShowRequestPermissionRationale(
        activity: Activity?,
        permission: String
    ): Boolean {
        if (activity != null) return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permission
        )

        return false
    }

    fun isPermissionDeclared(context: Context, permission: String): Boolean {
        val packagePermissions = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        ).requestedPermissions

        return packagePermissions?.any { p -> permission == p }
            ?: false
    }

    private fun setInitialRequestCalled(context: Context, permission: String) {
        context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(permission, true)
            .apply()
    }

    private fun isInitialRequestCalled(context: Context, permission: String): Boolean {
        return context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
            .getBoolean(permission, false)
    }
}