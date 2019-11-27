package com.nguyenhoanglam.imagepicker.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hoanglam on 8/17/17.
 */

public class PreferenceHelper {

    private static final String PREFS_FILE_NAME = "ImagePicker";

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getBoolean(permission, true);
    }
}
