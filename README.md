## ImagePicker
A simple library to pick pictures from the gallery and camera.

## Download
Add it to your module's build.gradle with:
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

and:
```
dependencies {
    compile 'com.github.nguyenhoanglam:ImagePicker:1.0.0'
}
```

## How to use
### Start image picker activity

```
  public void startImagePicker() {
      Intent intent = new Intent(this, ImagePickerActivity.class);

      intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
      intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 10);
      intent.putExtra(ImagePickerActivity.INTENT_EXTRA_CAMERA, true);
      intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);

      startActivityForResult(intent, ImagePickerActivity.REQUEST_CODE_PICKER);
  }
```

### Get selected image uri

```
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
