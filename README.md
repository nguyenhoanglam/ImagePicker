ImagePicker
========

An Android library that supports selecting images from the device or from the camera.

[![](https://jitpack.io/v/nguyenhoanglam/ImagePicker.svg)](https://jitpack.io/#nguyenhoanglam/ImagePicker)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImagePicker-green.svg?style=true)](https://android-arsenal.com/details/1/4072)

Demo
--------

<img src="https://i.imgur.com/ZM09aU3.png" height="652" width="350"> <img src="https://i.imgur.com/Hs9nhVt.png" height="652" width="350">

What's new
--------
- Fixed bugs.
- Supported Android 10 (API 29).
- Updated to new UI (remove selection overlay, add indicator...).
- Converted Java code to Kotlin code.
- Upgraded Glide to v4.11, AndroidX to v1.1.0.
- Added new `rootDirectoryName`, `limitMessage`, `indicatorColor` and `isShowNumberIndicator` options.
- Replaced `savePath` option by `directoryName` option.
- Removed `keepScreenOn` option.

Installation
--------

Add the following maven repositories in root build.gradle:
```
allprojects {
    repositories {
        ...
        maven { url "https://maven.google.com" }
        maven { url "https://jitpack.io" }
    }
}
```

Add the following dependency in app build.gradle:
```
dependencies {
    implementation 'com.github.nguyenhoanglam:ImagePicker:1.4.0'
}
```

You have to migrate your project to support AndroidX by add following lines on gradle.properties file:
```
android.useAndroidX=true
android.enableJetifier=true
```

Add `android:requestLegacyExternalStorage="true"` attribute to the `application` tag in the `AndroidManifest.xml` file:
```xml
<application
    ...
    android:requestLegacyExternalStorage="true">

    ...

</application>
```

For any Java projects, please follow [this guide](https://developer.android.com/kotlin/add-kotlin) to add Kotlin to existing app.

Usage
--------

### Start ImagePicker
```java
ImagePicker.with(this)
           .setFolderMode(true)
           .setFolderTitle("Album")
           .setRootDirectoryName(Config.ROOT_DIR_DCIM)
           .setDirectoryName("Image Picker")
           .setMultipleMode(true)
           .setShowNumberIndicator(true)
           .setMaxSize(10)
           .setLimitMessage("You can select up to 10 images")
           .setSelectedImages(images)
           .setRequestCode(100)
           .start();
```

### Handle selected images

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
        val images: ArrayList<Image> = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
        // Do stuff with image's path or id. For example:
        for (image in images) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image.id.toString())
                Glide.with(context)
                     .load(uri)
                     .into(imageView)
            } else {
                Glide.with(context)
                     .load(image.path)
                     .into(imageView)
            }
        }
    }
    super.onActivityResult(requestCode, resultCode, data)   // This line is REQUIRED in fragment mode
}
```

### Methods's description

| Name | Description | Default
| --- | --- | :---: |
| `with` | Initialize ImagePicker with activity or fragment context |
| `setStatusBarColor` | Status bar color, require API >= 21 | `#000000`
| `setToolbarColor` | Toolbar color | `#212121`
| `setToolbarTextColor` | Toolbar text color | `#FFFFFF`
| `setToolbarIconColor` | Toolbar icon color | `#FFFFFF`
| `setBackgroundColor` | Background color | `#424242`
| `setProgressBarColor` | ProgressBar color | `#4CAF50`
| `setIndicatorColor` | Selected image's indicator color | `#1976D2`
| `setCameraOnly` | Start camera and return captured image | `false`
| `setMultipleMode` | Allow to select multiple images | `true`
| `setFolderMode` | Group images by folders | `false`
| `setFolderTitle` | Folder screen's title, require FolderMode = `true` | `Albums`
| `setImageTitle` | Image screen's title, require FolderMode = `false` | `Photos`
| `setDoneTitle` | Done button's title | `DONE`
| `setAlwaysShowDoneButton` | Show done button even though no image selected | `false`
| `setShowCamera` | Show camera button | `true`
| `setRootDirectoryName` | Public root directory of captured images, should be one of: `Config.ROOT_DIR_DCIM`, `Config.ROOT_DIR_PICTURES`, `Config.ROOT_DIR_DOWNLOAD`. | `DCIM`
| `setDirectoryName` | Root directory's sub folder of captured images | Application name
| `setShowNumberIndicator` | Show selected image's indicator as number | `false`
| `setMaxSize` | Max images can be selected | `Int.MAX_VALUE`
| `setLimitMessage` | Message to be displayed when total selected images exceeds max count |
| `setSelectedImages` | List of images that will be shown as selected in ImagePicker | empty list
| `setRequestCode` | Request code for starting ImagePicker | `100`
| `start` | Open ImagePicker |

License
--------

Copyright (c) 2020 Nguyen Hoang Lam

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
