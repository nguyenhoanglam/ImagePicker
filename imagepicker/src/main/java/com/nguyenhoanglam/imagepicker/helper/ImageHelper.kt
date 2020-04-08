package com.nguyenhoanglam.imagepicker.helper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.nguyenhoanglam.imagepicker.model.Image
import java.io.File
import java.util.*

/**
 * Created by hoanglam on 7/31/16.
 */
object ImageHelper {

    private fun getNameFromFilePath(path: String): String {
        return if (path.contains(File.separator)) {
            path.substring(path.lastIndexOf(File.separator) + 1)
        } else path
    }

    fun grantAppPermission(context: Context, intent: Intent, fileUri: Uri) {
        val resolvedIntentActivities = context.packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolvedIntentInfo in resolvedIntentActivities) {
            val packageName = resolvedIntentInfo.activityInfo.packageName
            context.grantUriPermission(packageName, fileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun revokeAppPermission(context: Context, fileUri: Uri) {
        context.revokeUriPermission(fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    fun singleListFromPath(path: String): List<Image> {
        val images: MutableList<Image> = ArrayList()
        images.add(Image(0, getNameFromFilePath(path), path))
        return images
    }

    fun isGifFormat(image: Image): Boolean {
        val extension = image.path.substring(image.path.lastIndexOf(".") + 1, image.path.length)
        return extension.equals("gif", ignoreCase = true)
    }
}