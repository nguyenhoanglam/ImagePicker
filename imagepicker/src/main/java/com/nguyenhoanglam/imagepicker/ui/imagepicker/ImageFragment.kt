/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.databinding.ImagepickerFragmentBinding
import com.nguyenhoanglam.imagepicker.helper.ImageHelper
import com.nguyenhoanglam.imagepicker.helper.LayoutManagerHelper
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectListener
import com.nguyenhoanglam.imagepicker.model.CallbackStatus
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.model.Result
import com.nguyenhoanglam.imagepicker.ui.adapter.ImagePickerAdapter
import com.nguyenhoanglam.imagepicker.widget.GridSpacingItemDecoration

class ImageFragment : BaseFragment() {

    private var _binding: ImagepickerFragmentBinding? = null
    private val binding get() = _binding!!

    private var bucketId: Long? = null
    private lateinit var gridCount: GridCount

    private lateinit var viewModel: ImagePickerViewModel
    private lateinit var imageAdapter: ImagePickerAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var itemDecoration: GridSpacingItemDecoration

    companion object {

        const val BUCKET_ID = "BucketId"
        const val GRID_COUNT = "GridCount"

        fun newInstance(bucketId: Long, gridCount: GridCount): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putLong(BUCKET_ID, bucketId)
            args.putParcelable(GRID_COUNT, gridCount)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(gridCount: GridCount): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putParcelable(GRID_COUNT, gridCount)
            fragment.arguments = args
            return fragment
        }
    }

    private val selectedImageObserver = object : Observer<ArrayList<Image>> {
        override fun onChanged(it: ArrayList<Image>) {
            imageAdapter.setSelectedImages(it)
            viewModel.selectedImages.removeObserver(this)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bucketId = arguments?.getLong(BUCKET_ID)
        gridCount = arguments?.getParcelable(GRID_COUNT)!!

        viewModel = requireActivity().run {
            ViewModelProvider(this, ImagePickerViewModelFactory(requireActivity().application)).get(
                ImagePickerViewModel::class.java
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImagepickerFragmentBinding.inflate(inflater, container, false)

        binding.root.setBackgroundColor(
            viewModel.getConfig()
                .getBackgroundColor()
        )

        imageAdapter = ImagePickerAdapter(
            requireActivity(),
            viewModel.getConfig(),
            activity as OnImageSelectListener
        )
        gridLayoutManager = LayoutManagerHelper.newInstance(requireContext(), gridCount)
        itemDecoration = GridSpacingItemDecoration(
            gridLayoutManager.spanCount,
            resources.getDimension(R.dimen.imagepicker_grid_spacing).toInt()
        )

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            addItemDecoration(itemDecoration)
            adapter = imageAdapter
        }

        viewModel.result.observe(viewLifecycleOwner, {
            handleResult(it)
        })

        viewModel.selectedImages.observe(viewLifecycleOwner, selectedImageObserver)

        return binding.root
    }


    private fun handleResult(result: Result) {
        if (result.status is CallbackStatus.SUCCESS) {
            val images = ImageHelper.filterImages(result.images, bucketId)
            if (images.isNotEmpty()) {
                imageAdapter.setData(images)
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.GONE
            }
        } else {
            binding.recyclerView.visibility = View.GONE
        }
        binding.emptyText.visibility =
            if (result.status is CallbackStatus.SUCCESS && result.images.isEmpty()) View.VISIBLE else View.GONE
        binding.progressWheel.visibility =
            if (result.status is CallbackStatus.FETCHING) View.VISIBLE else View.GONE
    }

    override fun handleOnConfigurationChanged() {
        val newSpanCount =
            LayoutManagerHelper.getSpanCountForCurrentConfiguration(requireContext(), gridCount)
        itemDecoration =
            GridSpacingItemDecoration(
                gridLayoutManager.spanCount,
                resources.getDimension(R.dimen.imagepicker_grid_spacing).toInt()
            )
        gridLayoutManager.spanCount = newSpanCount
        binding.recyclerView.addItemDecoration(itemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}