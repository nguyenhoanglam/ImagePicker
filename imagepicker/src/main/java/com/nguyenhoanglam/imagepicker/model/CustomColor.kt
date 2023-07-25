/*
 * Copyright (C) 2023 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomColor(
    var background: String? = null,
    var statusBar: String? = null,
    var toolbar: String? = null,
    var toolbarTitle: String? = null,
    var toolbarIcon: String? = null,
    var doneButtonTitle: String? = null,
    var snackBarBackground: String? = null,
    var snackBarMessage: String? = null,
    var snackBarButtonTitle: String? = null,
    var loadingIndicator: String? = null,
    var selectedImageIndicator: String? = null,
) : Parcelable
