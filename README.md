## ImagePicker
A simple library to select images from the gallery and camera.

[![](https://jitpack.io/v/nguyenhoanglam/ImagePicker.svg)](https://www.jitpack.io/#nguyenhoanglam/ImagePicker)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImagePicker-green.svg?style=true)](https://android-arsenal.com/details/1/4072)

## Screenshot

<img src="https://cloud.githubusercontent.com/assets/4979755/18304733/46cfad58-750e-11e6-9a6c-129ece6cfc7d.png" height="683" width="384">
<img src="https://cloud.githubusercontent.com/assets/4979755/18304727/44117484-750e-11e6-8ad1-85301a171690.png" height="683" width="384">

## Download
Add to your module's build.gradle:
```java
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

and:
```java
dependencies {
    compile 'com.github.nguyenhoanglam:ImagePicker:1.1.3'
}
```

## How to use
### Start image picker activity
- Quick call
```java
ImagePicker.create(this)
            .folderMode(true) // folder mode (false by default)
            .folderTitle("Folder") // folder selection title
            .imageTitle("Tap to select") // image selection title
            .single() // single mode
            .multi() // multi mode (default mode)
            .limit(10) // max images can be selected (999 by default)
            .showCamera(true) // show camera or not (true by default)
            .imageDirectory("Camera") // directory name for captured image  ("Camera" folder by default)
            .origin(images) // original selected images, used in multi mode
            .start(REQUEST_CODE_PICKER); // start image picker activity with request code
```                
- If you want to deselect all selected images, do not call origin() method:
```java
 ImagePicker.create(this)
                .start(REQUEST_CODE_PICKER);
```
- Or use traditional Intent
```java
Intent intent = new Intent(this, ImagePickerActivity.class);

intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_MODE, true);
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 10);
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_FOLDER_TITLE, "Album");
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_TITLE, "Tap to select images");
intent.putExtra(ImagePickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, "Camera");

startActivityForResult(intent, REQUEST_CODE_PICKER);
```        
### Receive result

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
        ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
        // do your logic ....
    }
}
```

##Thanks
- Darshan Dorai for [MultipleImageSelect](https://github.com/darsh2/MultipleImageSelect) 
- [Glide](https://github.com/bumptech/glide) for image loading implementation

##License
Copyright 2016 Nguyen Hoang Lam

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
