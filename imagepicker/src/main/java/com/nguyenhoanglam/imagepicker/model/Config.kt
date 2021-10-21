/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.model

import android.graphics.Color
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Config() : Parcelable {

    private lateinit var toolbarColor: String
    private lateinit var statusBarColor: String
    var isLightStatusBar = false
    private lateinit var toolbarTextColor: String
    private lateinit var toolbarIconColor: String
    private lateinit var progressBarColor: String
    private lateinit var backgroundColor: String
    private lateinit var indicatorColor: String
    var isCameraOnly = false
    var isMultipleMode = false
    var isFolderMode = false
    var isShowNumberIndicator = false
    var isShowCamera = false
    var maxSize = MAX_SIZE
    lateinit var folderGridCount: GridCount
    lateinit var imageGridCount: GridCount
    lateinit var doneTitle: String
    lateinit var folderTitle: String
    lateinit var imageTitle: String
    var limitMessage: String? = null
    lateinit var rootDirectory: String
    lateinit var subDirectory: String
    var isAlwaysShowDoneButton = false
    lateinit var selectedImages: ArrayList<Image>

    constructor(parcel: Parcel) : this() {
        toolbarColor = parcel.readString()!!
        statusBarColor = parcel.readString()!!
        isLightStatusBar = parcel.readByte() != 0.toByte()
        toolbarTextColor = parcel.readString()!!
        toolbarIconColor = parcel.readString()!!
        progressBarColor = parcel.readString()!!
        backgroundColor = parcel.readString()!!
        indicatorColor = parcel.readString()!!
        isCameraOnly = parcel.readByte() != 0.toByte()
        isMultipleMode = parcel.readByte() != 0.toByte()
        isFolderMode = parcel.readByte() != 0.toByte()
        isShowNumberIndicator = parcel.readByte() != 0.toByte()
        isShowCamera = parcel.readByte() != 0.toByte()
        maxSize = parcel.readInt()
        folderGridCount = parcel.readParcelable(GridCount::class.java.classLoader)!!
        imageGridCount = parcel.readParcelable(GridCount::class.java.classLoader)!!
        doneTitle = parcel.readString()!!
        folderTitle = parcel.readString()!!
        imageTitle = parcel.readString()!!
        limitMessage = parcel.readString()
        rootDirectory = parcel.readString()!!
        subDirectory = parcel.readString()!!
        isAlwaysShowDoneButton = parcel.readByte() != 0.toByte()
        selectedImages = parcel.createTypedArrayList(Image.CREATOR)!!
    }


    fun getToolbarColor(): Int {
        return Color.parseColor(toolbarColor)
    }

    fun setToolbarColor(toolbarColor: String) {
        this.toolbarColor = toolbarColor
    }

    fun getStatusBarColor(): Int {
        return Color.parseColor(statusBarColor)
    }

    fun setStatusBarColor(statusBarColor: String) {
        this.statusBarColor = statusBarColor
    }

    fun getToolbarTextColor(): Int {
        return Color.parseColor(toolbarTextColor)
    }

    fun setToolbarTextColor(toolbarTextColor: String) {
        this.toolbarTextColor = toolbarTextColor
    }

    fun getToolbarIconColor(): Int {
        return Color.parseColor(toolbarIconColor)
    }

    fun setToolbarIconColor(toolbarIconColor: String) {
        this.toolbarIconColor = toolbarIconColor
    }

    fun getProgressBarColor(): Int {
        return Color.parseColor(progressBarColor)
    }

    fun setProgressBarColor(progressBarColor: String) {
        this.progressBarColor = progressBarColor
    }

    fun getBackgroundColor(): Int {
        return Color.parseColor(backgroundColor)
    }

    fun setBackgroundColor(backgroundColor: String) {
        this.backgroundColor = backgroundColor
    }

    fun getIndicatorColor(): Int {
        return Color.parseColor(indicatorColor)
    }

    fun setIndicatorColor(indicatorColor: String) {
        this.indicatorColor = indicatorColor
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(toolbarColor)
        parcel.writeString(statusBarColor)
        parcel.writeByte(if (isLightStatusBar) 1 else 0)
        parcel.writeString(toolbarTextColor)
        parcel.writeString(toolbarIconColor)
        parcel.writeString(progressBarColor)
        parcel.writeString(backgroundColor)
        parcel.writeString(indicatorColor)
        parcel.writeByte(if (isCameraOnly) 1 else 0)
        parcel.writeByte(if (isMultipleMode) 1 else 0)
        parcel.writeByte(if (isFolderMode) 1 else 0)
        parcel.writeByte(if (isShowNumberIndicator) 1 else 0)
        parcel.writeByte(if (isShowCamera) 1 else 0)
        parcel.writeInt(maxSize)
        parcel.writeParcelable(folderGridCount, flags)
        parcel.writeParcelable(imageGridCount, flags)
        parcel.writeString(doneTitle)
        parcel.writeString(folderTitle)
        parcel.writeString(imageTitle)
        parcel.writeString(limitMessage)
        parcel.writeString(rootDirectory)
        parcel.writeString(subDirectory)
        parcel.writeByte(if (isAlwaysShowDoneButton) 1 else 0)
        parcel.writeTypedList(selectedImages)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Config> {

        const val EXTRA_CONFIG = "ImagePickerConfig"
        const val EXTRA_IMAGES = "ImagePickerImages"
        const val RC_READ_EXTERNAL_STORAGE_PERMISSION = 1000
        const val RC_WRITE_EXTERNAL_STORAGE_PERMISSION = 1001
        const val MAX_SIZE = Int.MAX_VALUE
        val ROOT_DIR_DCIM: String = Environment.DIRECTORY_DCIM
        val ROOT_DIR_DOWNLOAD: String = Environment.DIRECTORY_DOWNLOADS
        val ROOT_DIR_PICTURES: String = Environment.DIRECTORY_PICTURES

        override fun createFromParcel(parcel: Parcel): Config {
            return Config(parcel)
        }

        override fun newArray(size: Int): Array<Config?> {
            return arrayOfNulls(size)
        }
    }
}