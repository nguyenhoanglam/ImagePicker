package com.imagepicker.example.java;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePickerLauncher;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final ImagePickerLauncher launcher = ImagePicker.registerImagePicker(
            this,
            new JvmContextProvider(this),
            new JvmImagePickerCallback() {
                @Override
                public void onImagePickerResult(ArrayList<Image> images) {
                    // TODO handle image result
                }
            }
    );

    private final ImagePickerLauncher launcherLambda = ImagePicker.registerImagePicker(
            this,
            () -> this,
            (ArrayList<Image> images) -> {
                // TODO handle image result
                return null;
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePickerConfig config = new ImagePickerConfig();
        // TODO config.setSomething();
        launcher.launch(config);
    }
}
