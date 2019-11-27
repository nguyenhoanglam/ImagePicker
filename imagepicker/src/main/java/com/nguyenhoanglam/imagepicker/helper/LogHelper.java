package com.nguyenhoanglam.imagepicker.helper;

import android.util.Log;

public class LogHelper {

    private static final String TAG = "ImagePicker";

    private static LogHelper INSTANCE;

    private boolean isEnable = true;

    private LogHelper() {
    }

    public static LogHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogHelper();
        }
        return INSTANCE;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public void d(String message) {
        if (isEnable) {
            Log.d(TAG, message);
        }
    }

    public void e(String message) {
        if (isEnable) {
            Log.e(TAG, message);
        }
    }

    public void w(String message) {
        if (isEnable) {
            Log.w(TAG, message);
        }
    }
}
