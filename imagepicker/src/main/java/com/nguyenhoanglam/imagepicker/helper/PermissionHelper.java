package com.nguyenhoanglam.imagepicker.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


/**
 * Created by hoanglam on 8/21/17.
 */

public class PermissionHelper {

    public static void checkPermission(Activity activity, String permission, PermissionAskListener listener) {
        if (!hasSelfPermission(activity, permission)) {
            if (shouldShowRequestPermissionRationale(activity, permission)) {
                listener.onPermissionPreviouslyDenied();
            } else {
                if (PreferenceHelper.isFirstTimeAskingPermission(activity, permission)) {
                    PreferenceHelper.firstTimeAskingPermission(activity, permission, false);
                    listener.onNeedPermission();
                } else {
                    listener.onPermissionDisabled();
                }
            }
        } else {
            listener.onPermissionGranted();
        }

    }

    public static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        activity.startActivity(intent);
    }

    public static String[] asArray(@NonNull String... permissions) {
        if (permissions.length == 0) {
            throw new IllegalArgumentException("There is no given permission");
        }

        final String[] dest = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            dest[i] = permissions[i];
        }
        return dest;
    }

    public static boolean hasGranted(int grantResult) {
        return grantResult == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (!hasGranted(result)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSelfPermission(Context context, String permission) {
        if (shouldAskPermission()) {
            return permissionHasGranted(context, permission);
        }
        return true;
    }

    public static boolean hasSelfPermissions(Context context, String[] permissions) {
        if (shouldAskPermission()) {
            for (String permission : permissions) {
                if (!permissionHasGranted(context, permission)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestAllPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        if (shouldAskPermission()) {
            internalRequestPermissions(activity, permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void internalRequestPermissions(Activity activity, String[] permissions, int requestCode) {
        if (activity == null) {
            throw new IllegalArgumentException("Given activity is null.");
        }
        activity.requestPermissions(permissions, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static boolean permissionHasGranted(Context context, String permission) {
        return hasGranted(context.checkSelfPermission(permission));
    }

    private static boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public interface PermissionAskListener {
        void onNeedPermission();

        void onPermissionPreviouslyDenied();

        void onPermissionDisabled();

        void onPermissionGranted();
    }

}
