/*
 * Copyright (C) 2021 Image Picker
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

sealed class CallbackStatus {
    object IDLE : CallbackStatus()
    object FETCHING : CallbackStatus()
    object SUCCESS : CallbackStatus()
}