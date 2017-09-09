package com.nguyenhoanglam.imagepicker.helper;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;

import com.nguyenhoanglam.imagepicker.R;

/**
 * Created by hoanglam on 8/17/17.
 */

public class CameraHelper {

    public static boolean checkCameraAvailability(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean isAvailable = intent.resolveActivity(context.getPackageManager()) != null;

        if (!isAvailable) {
            Context appContext = context.getApplicationContext();
            Toast.makeText(appContext,
                    appContext.getString(R.string.imagepicker_error_no_camera), Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }
}
