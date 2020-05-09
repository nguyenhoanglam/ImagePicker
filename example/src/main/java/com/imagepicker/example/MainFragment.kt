/*
 * Copyright (c) 2020 Nguyen Hoang Lam.
 * All rights reserved.
 */
package com.imagepicker.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*

class MainFragment : Fragment() {

    private var config: Config? = null
    private var adapter: ImageAdapter? = null
    private var images = ArrayList<Image>()

    companion object {
        const val EXTRA_CONFIG = "Config"
        fun newInstance(config: Config?): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_CONFIG, config)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = arguments!!.getParcelable(EXTRA_CONFIG)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ImageAdapter(activity!!)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

        pickImageButton.setOnClickListener { start() }
    }

    private fun start() {
        ImagePicker.with(this)
            .setFolderMode(config!!.isFolderMode)
            .setFolderTitle(config!!.folderTitle)
            .setRootDirectoryName(config!!.rootDirectoryName)
            .setDirectoryName(config!!.directoryName)
            .setMultipleMode(config!!.isMultipleMode)
            .setShowNumberIndicator(config!!.isShowNumberIndicator)
            .setSelectedImages(config!!.selectedImages)
            .setRequestCode(config!!.requestCode)
            .setCameraOnly(config!!.isCameraOnly)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES)
            adapter!!.setData(images)
        }
    }
}