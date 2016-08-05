## ImagePicker
A simple library to pick pictures from the gallery and camera.

## Download
Add it to your module's build.gradle with:
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
    compile 'com.github.nguyenhoanglam:ImagePicker:1.0.0'
}
```

## How to use
### Start image picker activity
```java
    ImagePicker.create(this)
                .single() // single mode
                .limit(10) // multi mode (default mode)
                .showCamera(true) // show camera or not (true by default)
                .origin(images) // original selected images, used in multi mode
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
```                

### Extra options
```java
1. ImagePickerActivity.INTENT_EXTRA_MODE: define single image or multiple images selection mode
2. ImagePickerActivity.INTENT_EXTRA_LIMIT: max number of images can be selected (default = 99)
3. ImagePickerActivity.INTENT_EXTRA_CAMERA: show camera button to capture image
4. ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES: selected images that needed to show in picker activity
```

### Get selected image uri

```java
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == ImagePickerActivity.REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null) {
          ArrayList<Image> images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
          StringBuffer stringBuffer = new StringBuffer();
          for (int i = 0, l = images.size(); i < l; i++) {
              stringBuffer.append(images.get(i).path + "\n");
          }
          textView.setText(stringBuffer.toString());
      }
  }
```  
