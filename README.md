## ImagePicker
A simple library that allows you to select images from the device library or directly from the camera

[![](https://jitpack.io/v/nguyenhoanglam/ImagePicker.svg)](https://www.jitpack.io/#nguyenhoanglam/ImagePicker)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ImagePicker-green.svg?style=true)](https://android-arsenal.com/details/1/4072)

## Screenshot

<img src="https://user-images.githubusercontent.com/4979755/29860123-18cdb978-8d8f-11e7-9020-535f9507c993.png" height="683" width="384">
<img src="https://user-images.githubusercontent.com/4979755/29860122-18c6ed5a-8d8f-11e7-9726-e916c0c4670e.png" height="683" width="384">

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
    compile 'com.github.nguyenhoanglam:ImagePicker:1.2.0'
}
```

## How to use

```java
ImagePicker.with(this)                         //  Initialize ImagePicker with activity or fragment context
           .setToolbarColor("#212121")         //  Toolbar color
           .setStatusBarColor("#000000")       //  StatusBar color (works with SDK >= 21  )
           .setToolbarTextColor("#FFFFFF")     //  Toolbar text color (Title and Done button)
           .setToolbarIconColor("#FFFFFF")     //  Toolbar icon color (Back and Camera button)
           .setProgressBarColor("#4CAF50")     //  ProgressBar color
           .setBackgroundColor("#212121")      //  Background color
           .setCameraOnly(false)               //  Camera mode
           .setMultipleMode(true)              //  Select multiple images or single image
           .setFolderMode(true)                //  Folder mode
           .setShowCamera(true)                //  Show camera button
           .setFolderTitle("Albums")           //  Folder title (works with FolderMode = true)
           .setImageTitle("Galleries")         //  Image title (works with FolderMode = false)
           .setDoneTitle("Done")               //  Done button title
           .setMaxSize(10)                     //  Max images can be selected
           .setSavePath("ImagePicker")         //  Image capture folder name
           .setSelectedImages(images)          //  Selected images
           .start();                           //  Start ImagePicker    
```

### Receive result

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
        ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
        // do your logic here...
    }
    super.onActivityResult(requestCode, resultCode, data);
}
```

##License
Copyright 2016 Nguyen Hoang Lam

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
