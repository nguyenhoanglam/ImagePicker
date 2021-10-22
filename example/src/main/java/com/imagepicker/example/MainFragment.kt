/*
 * Copyright (c) 2020 Nguyen Hoang Lam.
 * All rights reserved.
 */
package com.imagepicker.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.imagepicker.example.databinding.FragmentMainBinding
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.ImagePickerConfig
import com.nguyenhoanglam.imagepicker.ui.imagepicker.registerImagePicker
import java.util.*

class MainFragment : Fragment() {

    private var config: ImagePickerConfig? = null
    private var adapter: ImageAdapter? = null
    private var images = ArrayList<Image>()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val launcher = registerImagePicker {
        adapter!!.setData(it)
    }

    companion object {
        const val EXTRA_CONFIG = "Config"
        fun newInstance(config: ImagePickerConfig?): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_CONFIG, config)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = requireArguments().getParcelable(EXTRA_CONFIG)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ImageAdapter(requireActivity())
        binding.recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

        binding.pickImageButton.setOnClickListener { start() }
    }

    private fun start() {
        launcher.launch(config!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}