ImagePicker
========

An android library which help selecting images from the device with customizable UI.

[![](https://jitpack.io/v/nguyenhoanglam/ImagePicker.svg)](https://jitpack.io/#nguyenhoanglam/ImagePicker)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImagePicker-green.svg?style=true)](https://android-arsenal.com/details/1/4072)

Preview
--------

<img src="https://i.imgur.com/ZM09aU3.png" height="652" width="350"> <img src="https://i.imgur.com/d3O0VFN.png" height="652" width="350">

Changelog
--------
- Reimplement underlying logic to adapt to Android Q and later.
- Replace `startActivityForResult` with `ActivityResultLauncher` mechanism.
- Add and rename some configuration attributes: `isLightStatusBar`, `imageGridCount` ...
- Remove `id` and `path` attribute from `Image` class.
- Make some small UI changes.

Installation
--------

In `settings.gradle` file, add JitPack maven like below:
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependency in app build.gradle:
```
dependencies {
    implementation 'com.github.nguyenhoanglam:ImagePicker:1.5.1'
}
```

You have to migrate your project to support AndroidX by add following lines on gradle.properties file:
```
android.useAndroidX=true
android.enableJetifier=true
```

For any Java projects, please follow [this guide](https://developer.android.com/kotlin/add-kotlin) to add Kotlin to existing app.

Usage
--------

Define an `ActivityResultLauncher` class variable in `Activity` or `Fragment`.
```java
private val launcher = registerImagePicker { images ->
    // Selected images are ready to use
    if(images.isNotEmpty()){
        val sampleImage = images[0]
        Glide.with(this@MainActivity)
             .load(sampleImage.uri)
             .into(imageView)
    }
}
```

Then, launch image picker when needed.
- With default configuration:
```java
launcher.launch()
```
- With customize configuration:
```java
val config = ImagePickerConfig(
    statusBarColor = "#000000",
    isLightStatusBar = false,
    isFolderMode = true,
    isMultipleMode = true,
    maxSize = 10,
    rootDirectory = Config.ROOT_DIR_DOWNLOAD,
    subDirectory = "Photos",
    folderGridCount = GridCount(2, 4),
    imageGridCount = GridCount(3, 5),
    // See more at configuration attributes table below
)

launcher.launch(config)
```

Configuration attributes
--------

| Name | Description | Default
| --- | --- | :---: |
| `statusBarColor` | Status bar color (require API >= 21) | `#000000`
| `isLightStatusBar` | Set status bar to light/dark mode to change it's content to dark/light (require API >= 21) | `false`
| `toolbarColor` | Toolbar color | `#212121`
| `toolbarTextColor` | Toolbar text color | `#FFFFFF`
| `toolbarIconColor` | Toolbar icon color | `#FFFFFF`
| `backgroundColor` | Background color | `#424242`
| `progressIndicatorColor` | Loading indicator color | `#009688`
| `selectedIndicatorColor` | Selected image's indicator color | `#1976D2`
| `isCameraOnly` | Open camera, then capture and return an image   | `false`
| `isMultipleMode` | Allow to select multiple images | `true`
| `isFolderMode` | Show images by folders | `false`
| `folderGridCount` | Set folder colums for portrait and landscape orientation | `GridCount(2, 4)`
| `imageGridCount` | Set image colums for portrait and landscape orientation | `GridCount(3, 5)`
| `doneTitle` | Done button title | `DONE`
| `folderTitle` | Toolbar title for folder mode (require FolderMode = `true`) | `Albums`
| `imageTitle` | Toolbar title for image mode (require FolderMode = `false`) | `Photos`
| `isShowCamera` | Show camera button | `true`
| `isShowNumberIndicator` | Show selected image's indicator as number | `false`
| `isAlwaysShowDoneButton` | Show done button even though no images've been selected yet | `false`
| `rootDirectory` | Public root directory of captured image, should be one of: `RootDirectory.DCIM`, `RootDirectory.PICTURES`, `RootDirectory.DOWNLOADS`. | `RootDirectory.DCIM`
| `subDirectory` | Root directory's sub folder of captured image | Application name
| `maxSize` | Max images can be selected | `Int.MAX_VALUE`
| `limitMessage` | Message to be displayed when total selected images exceeds max size | ...
| `selectedImages` | List of images that will be shown as selected in ImagePicker | Empty list

License
--------

Copyright (c) 2021 Nguyen Hoang Lam