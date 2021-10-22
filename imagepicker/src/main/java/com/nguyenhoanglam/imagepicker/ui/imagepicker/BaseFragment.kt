/*
 * Copyright (C) 2021 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.content.res.Configuration
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handleOnConfigurationChanged()
    }

    abstract fun handleOnConfigurationChanged()
}