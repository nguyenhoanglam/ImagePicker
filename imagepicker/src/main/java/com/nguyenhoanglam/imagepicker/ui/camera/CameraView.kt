package com.nguyenhoanglam.imagepicker.ui.camera

import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.common.MvpView

/**
 * Created by hoanglam on 8/22/17.
 */
interface CameraView : MvpView {
    fun finishPickImages(images: List<Image>)
}