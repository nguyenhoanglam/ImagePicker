package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.adapter.FolderPickerAdapter
import com.nguyenhoanglam.imagepicker.adapter.ImagePickerAdapter
import com.nguyenhoanglam.imagepicker.listener.OnBackAction
import com.nguyenhoanglam.imagepicker.listener.OnFolderClickListener
import com.nguyenhoanglam.imagepicker.listener.OnImageClickListener
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectionListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.widget.GridSpacingItemDecoration
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by hoanglam on 8/17/17.
 */
class RecyclerViewManager(private val recyclerView: RecyclerView, private val config: Config, orientation: Int) {

    private val context: Context = recyclerView.context
    private lateinit var layoutManager: GridLayoutManager
    private var itemOffsetDecoration: GridSpacingItemDecoration? = null
    private lateinit var imageAdapter: ImagePickerAdapter
    private lateinit var folderAdapter: FolderPickerAdapter
    private var imageColumns = 0
    private var folderColumns = 0
    private val imageLoader: ImageLoader
    private var foldersState: Parcelable? = null
    private lateinit var title: String
    private var isShowingFolder: Boolean

    fun setupAdapters(imageClickListener: OnImageClickListener, folderClickListener: OnFolderClickListener) {
        var selectedImages: ArrayList<Image> = arrayListOf()
        if (config.isMultipleMode && !config.selectedImages.isEmpty()) {
            selectedImages = config.selectedImages
        }
        imageAdapter = ImagePickerAdapter(context, config, imageLoader, selectedImages, imageClickListener)
        folderAdapter = FolderPickerAdapter(context, imageLoader, object : OnFolderClickListener {
            override fun onFolderClick(folder: Folder) {
                foldersState = recyclerView.layoutManager!!.onSaveInstanceState()
                folderClickListener.onFolderClick(folder)
            }
        })
    }

    /**
     * Set item size, column size base on the screen orientation
     */
    fun changeOrientation(orientation: Int) {
        imageColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 5
        folderColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
        val columns = if (isShowingFolder) folderColumns else imageColumns
        layoutManager = GridLayoutManager(context, columns)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        setItemDecoration(columns)
    }

    private fun setItemDecoration(columns: Int) {
        if (itemOffsetDecoration != null) {
            recyclerView.removeItemDecoration(itemOffsetDecoration!!)
        }
        itemOffsetDecoration = GridSpacingItemDecoration(columns,
                context.resources.getDimensionPixelSize(R.dimen.imagepicker_item_padding),
                false
        )
        recyclerView.addItemDecoration(itemOffsetDecoration!!)
        layoutManager.spanCount = columns
    }

    fun setOnImageSelectionListener(imageSelectionListener: OnImageSelectionListener?) {
        checkAdapterIsInitialized()
        imageAdapter.setOnImageSelectionListener(imageSelectionListener)
    }

    val selectedImages: List<Image>
        get() {
            checkAdapterIsInitialized()
            return imageAdapter.getSelectedImages()
        }

    fun addSelectedImages(images: List<Image>) {
        imageAdapter.addSelected(images)
    }

    private fun checkAdapterIsInitialized() {
        checkNotNull(imageAdapter) { "Must call setupAdapters first!" }
    }

    fun selectImage(): Boolean {
        if (config.isMultipleMode) {
            if (imageAdapter!!.getSelectedImages().size >= config.maxSize) {
                return false
            }
        } else {
            if (imageAdapter!!.itemCount > 0) {
                imageAdapter!!.removeAllSelected()
            }
        }
        return true
    }

    fun handleBack(action: OnBackAction) {
        if (config.isFolderMode && !isShowingFolder) {
            setFolderAdapter(null)
            action.onBackToFolder()
            return
        }
        action.onFinishImagePicker()
    }

    fun setImageAdapter(images: List<Image>, title: String) {
        imageAdapter.setData(images)
        setItemDecoration(imageColumns)
        recyclerView.adapter = imageAdapter
        this.title = title
        isShowingFolder = false
    }

    fun setFolderAdapter(folders: List<Folder>?) {
        folderAdapter!!.setData(folders)
        setItemDecoration(folderColumns)
        recyclerView.adapter = folderAdapter
        isShowingFolder = true
        if (foldersState != null) {
            layoutManager!!.spanCount = folderColumns
            recyclerView.layoutManager!!.onRestoreInstanceState(foldersState)
        }
    }

    fun getTitle(): String? {
        return if (isShowingFolder) {
            config.folderTitle
        } else if (config.isFolderMode) {
            title
        } else {
            config.imageTitle
        }
    }

    val isShowDoneButton: Boolean
        get() = config.isMultipleMode && (config.isAlwaysShowDoneButton || imageAdapter!!.getSelectedImages().size > 0)

    init {
        changeOrientation(orientation)
        imageLoader = ImageLoader()
        isShowingFolder = config.isFolderMode
    }
}