/*
 * Copyright (C) 2021 The Android Open Source Project
 * Author: Nguyen Hoang Lam <hoanglamvn90@gmail.com>
 */

package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.databinding.ImagepickerFragmentBinding
import com.nguyenhoanglam.imagepicker.helper.ImageHelper
import com.nguyenhoanglam.imagepicker.helper.LayoutManagerHelper
import com.nguyenhoanglam.imagepicker.listener.OnFolderClickListener
import com.nguyenhoanglam.imagepicker.model.CallbackStatus
import com.nguyenhoanglam.imagepicker.model.GridCount
import com.nguyenhoanglam.imagepicker.model.Result
import com.nguyenhoanglam.imagepicker.ui.adapter.FolderPickerAdapter
import com.nguyenhoanglam.imagepicker.widget.GridSpacingItemDecoration

class FolderFragment : BaseFragment() {

    private var _binding: ImagepickerFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridCount: GridCount

    private var viewModel: ImagePickerViewModel? = null
    private lateinit var folderAdapter: FolderPickerAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var itemDecoration: GridSpacingItemDecoration

    companion object {
        const val GRID_COUNT = "GridCount"

        fun newInstance(gridCount: GridCount): FolderFragment {
            val fragment = FolderFragment()
            val args = Bundle()
            args.putParcelable(ImageFragment.GRID_COUNT, gridCount)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gridCount = arguments?.getParcelable(FolderFragment.GRID_COUNT)!!

        viewModel = activity?.run {
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
            viewModel!!.getConfig()
                .getBackgroundColor()
        )

        folderAdapter = FolderPickerAdapter(requireActivity(), activity as OnFolderClickListener)
        gridLayoutManager = LayoutManagerHelper.newInstance(requireContext(), gridCount)
        itemDecoration = GridSpacingItemDecoration(
            gridLayoutManager.spanCount,
            resources.getDimension(R.dimen.imagepicker_grid_spacing).toInt()
        )

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            addItemDecoration(itemDecoration)
            adapter = folderAdapter
        }
        viewModel?.result?.observe(viewLifecycleOwner, {
            handleResult(it)
        })

        return binding.root
    }


    private fun handleResult(result: Result) {
        if (result.status is CallbackStatus.SUCCESS && result.images.isNotEmpty()) {
            val folders = ImageHelper.folderListFromImages(result.images)
            folderAdapter.setData(folders)
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.GONE
        }
        binding.emptyText.visibility =
            if (result.status is CallbackStatus.SUCCESS && result.images.isEmpty()) View.VISIBLE else View.GONE
        binding.progressWheel.visibility =
            if (result.status is CallbackStatus.FETCHING) View.VISIBLE else View.GONE
    }

    override fun handleOnConfigurationChanged() {
        binding.recyclerView.removeItemDecoration(itemDecoration)

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