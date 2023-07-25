# ImagePicker

An Android library that allows you to select images from the device library or camera with a handy UI customization.

## Preview

<img src="https://i.imgur.com/ZM09aU3.png" height="652" width="350"> <img src="https://i.imgur.com/d3O0VFN.png" height="652" width="350">

## Changelog

- Fix camera permission bug: [#148](https://github.com/nguyenhoanglam/ImagePicker/issues/148), [#155](https://github.com/nguyenhoanglam/ImagePicker/issues/155)
- Allow to set custom colors, drawables, texts and messages.
- Add select and unselect all feature.
- Add, remove and rename some options.
- Upgrade gradle and dependencies version.

## Installation

In `settings.gradle` file, add JitPack maven as below:
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency in app build.gradle:
```
dependencies {
    implementation 'com.github.nguyenhoanglam:ImagePicker:1.6.1'
}
```

## Usage

Define an `ActivityResultLauncher` class variable in `Activity` or `Fragment`.
```java
private val launcher = registerImagePicker { images ->
        // selected images
        if(images.isNotEmpty()){
        val image = images[0]
        Glide.with(this@MainActivity)
        .load(image.uri)
        .into(imageView)
        }
        }
```

Then launch the picker
- with default configuration:
```java
launcher.launch()
```
- with custom configuration:
```java
val config = ImagePickerConfig(            
    isFolderMode = true,
    isShowCamera = true,
    limitSize = 10,
    selectedIndicatorType = IndicatorType.NUMBER,
    rootDirectory = RootDirectory.DCIM,
    subDirectory = "Image Picker",
    folderGridCount = GridCount(2, 4),
    imageGridCount = GridCount(3, 5),
    customColor = CustomColor(
        background = "#000000",
        statusBar = "#000000",
        toolbar = "#212121",
        toolbarTitle = "#FFFFFF",
        toolbarIcon = "#FFFFFF",
        // ...
    ),
    customMessage = CustomMessage(
        reachLimitSize = "You can only select up to 10 images.",
        noImage = "No image found.",
        noPhotoAccessPermission = "Please allow permission to access photos and media.",
        noCameraPermission = "Please allow permission to access camera."
        // ...
    ),
    customDrawable = CustomDrawable(
        cameraIcon = R.drawable.ic_camera,
        selectAllIcon = R.drawable.ic_select_all,
        unselectAllIcon = R.drawable.ic_unselect_all,
        loadingImagePlaceholder = R.drawable.img_loading_placeholder
        // ...
    ),
    // see more options below
)

launcher.launch(config)
```

## Configuration options

| Option | Description | Default value
| --- | --- | :---: |
| `isCameraMode` | Capture an image from the camera instead of selecting from the library. If set to `true`, this option will ignore most of other options such as `isMultiSelectMode`, `isFolderMode`, `customColor`, `customIcon`... | `false`
| `isMultiSelectMode` | Allow to select multiple images. | `true`
| `isFolderMode` | Group images in folders. | `true`
| `isShowCamera` | Show the camera button. | `true`
| `isAlwaysShowDoneButton` | Show done button even if there's no image that has been selected yet. | `true`
| `isSelectAllEnabled` | Show select all images button (works if `isMultiSelectMode = true`). | `true`
| `isUnselectAllEnabled` | Show unselect all images button (works if `isMultiSelectMode = true`). | `true`
| `isImageTransitionEnabled` | Enable image transition. | `true`
| `statusBarContentMode` | Status bar content mode. | `StatusBarContent.LIGHT`
| `selectedIndicatorType` | Set selected image's indicator type. | `IndicatorType.NUMBER`
| `limitSize` | Maximum number of images that can be selected. | `Int.MAX_VALUE`
| `folderGridCount` | Number of folder columns for portrait and landscape orientation. | `GridCount(2, 4)`
| `imageGridCount` | Number of image columns for portrait and landscape orientation. | `GridCount(3, 5)`
| `doneButtonTitle` | Done button's title. | `DONE`
| `snackBarButtonTitle` | Snack bar action button's title. | `OK`
| `folderTitle` | Toolbar title for folder mode (works if `isFolderMode = true`). | `Albums`
| `imageTitle` | Toolbar title for image mode (works if `isFolderMode = false`). | `Photos`
| `rootDirectory` | Public root directory of the captured image. | `RootDirectory.DCIM`
| `subDirectory` | Subfolder of root directory where the captured image is saved. | *application name*
| `selectedImages` | Define images that will be marked as selected when launching picker. | *empty list*
| `customColor` | Custom colors: toolbar, indicator... | `CustomColor(...)`
| `customDrawable` | Custom drawable resources: back icon, camera icon, image placeholder... | `CustomDrawable(...)`
| `customMessage` | Custom messages: no permission, limit size, error... | `CustomMessage(...)`

#### *CustomColor* class

Define custom color for views, type = `String`.
| Property | Description | Default value
| --- | --- | :---: |
| `background` | Background color. | `#000000`
| `statusBar` | Status bar color. | `#000000`
| `toolbar` | Toolbar color. | `#212121`
| `toolbarTitle` | Toolbar title color. | `#FFFFFF`
| `toolbarIcon` | Toolbar icons color (this color will not be applied to `CustomDrawable` toolbar icons). | `#FFFFFF`
| `doneButtonTitle` | Done button title color. | `#FFFFFF`
| `snackBarBackground` | Snack bar background color. | `#323232`
| `snackBarMessage` | Snack bar message color. | `#FFFFFF`
| `snackBarButtonTitle` | Snack bar button title color. | `#4CAF50`
| `loadingIndicator` | Loading indicator color. | `#757575`
| `selectedImageIndicator` | Selected image indicator color. | `#1976D2`

#### *CustomDrawable* class

Define custom icon for the toolbar's buttons, type = `Int` (resource id). `24dp` icon size is recommended.
| Property | Description | Example
| --- | --- | :---: |
| `backIcon` | Back button icon. | `R.drawable.ic_back`
| `cameraIcon` | Camera button icon. | `R.drawable.ic_camera`
| `selectAllIcon` | Select all button icon. | `R.drawable.ic_select_all`
| `unselectAllIcon` | Unselect all button icon. | `R.drawable.ic_unselect_all`
| `loadingImagePlaceholder` | Placeholder for loading image. | `R.drawable.img_loading_placeholder`
| `errorImagePlaceholder` | Placeholder for error image. | `R.drawable.img_error_placeholder`

#### *CustomMessage* class

Define custom message when something's wrong, type = `String`.
| Property | Description | Default value
| --- | --- | :---: |
| `reachLimitSize` | Reached the limit number of images that can be selected. | `You have reached the limit number of images.`
| `cameraError` | Failed to open camera. | `Could not open camera.`
| `noCamera` | Device has no camera. | `No camera found.`
| `noImage` | Device has no image. | `No image available.`
| `noPhotoAccessPermission` | Photos and media access permission is not granted. | `Please allow permission to access photos and media.`
| `noCameraPermission` | Camera permisson is not granted. | `Please allow permission to access camera.`

Author
--------

Copyright (c) 2023 Nguyen Hoang Lam
- [LinkedIn](https://www.linkedin.com/in/lam-nguyen-hoang-70bb21115)
- [Facebook](https://www.facebook.com/hoanglamvn90)