package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.database.ContentObserver
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nguyenhoanglam.imagepicker.R
import com.nguyenhoanglam.imagepicker.helper.CameraHelper.checkCameraAvailability
import com.nguyenhoanglam.imagepicker.helper.LogHelper.Companion.instance
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.PermissionAskListener
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.checkPermission
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.hasGranted
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.openAppSettings
import com.nguyenhoanglam.imagepicker.helper.PermissionHelper.requestAllPermissions
import com.nguyenhoanglam.imagepicker.listener.OnBackAction
import com.nguyenhoanglam.imagepicker.listener.OnFolderClickListener
import com.nguyenhoanglam.imagepicker.listener.OnImageClickListener
import com.nguyenhoanglam.imagepicker.listener.OnImageSelectionListener
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.widget.ImagePickerToolbar
import com.nguyenhoanglam.imagepicker.widget.ProgressWheel
import com.nguyenhoanglam.imagepicker.widget.SnackBarView
import java.util.*

/**
 * Created by hoanglam on 7/31/16.
 */
class ImagePickerActivity : AppCompatActivity(), ImagePickerView {
    private lateinit var toolbar: ImagePickerToolbar
    private lateinit var recyclerViewManager: RecyclerViewManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressWheel: ProgressWheel
    private lateinit var noImageText: TextView
    private lateinit var snackBar: SnackBarView
    private lateinit var config: Config
    private var handler: Handler? = null
    private var observer: ContentObserver? = null
    private lateinit var presenter: ImagePickerPresenter
    private val logger = instance

    private val imageClickListener: OnImageClickListener = object : OnImageClickListener {
        override fun onImageClick(view: View, position: Int, isSelected: Boolean): Boolean {
            return recyclerViewManager.selectImage()
        }
    }
    private val folderClickListener: OnFolderClickListener = object : OnFolderClickListener {
        override fun onFolderClick(folder: Folder) {
            setImageAdapter(folder.images, folder.folderName)
        }
    }
    private val backClickListener = View.OnClickListener { onBackPressed() }
    private val cameraClickListener = View.OnClickListener { captureImageWithPermission() }
    private val doneClickListener = View.OnClickListener { onDone() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent == null) {
            finish()
            return
        }
        config = intent.getParcelableExtra(Config.EXTRA_CONFIG)
        setContentView(R.layout.imagepicker_activity_picker)
        setupViews()
        setupComponents()
        setupToolbar()
    }

    private fun setupViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        progressWheel = findViewById(R.id.progressWheel)
        noImageText = findViewById(R.id.noImageText)
        snackBar = findViewById(R.id.snackbar)
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = config.getStatusBarColor()
        }
        progressWheel.barColor = config.getProgressBarColor()
        findViewById<View>(R.id.container).setBackgroundColor(config.getBackgroundColor())
        progressWheel.visibility = View.GONE
        recyclerView.visibility = View.GONE
        noImageText.visibility = View.GONE
    }

    private fun setupComponents() {
        recyclerViewManager = RecyclerViewManager(recyclerView, config, resources.configuration.orientation)
        recyclerViewManager!!.setupAdapters(imageClickListener, folderClickListener)
        recyclerViewManager!!.setOnImageSelectionListener(object : OnImageSelectionListener {
            override fun onSelectionUpdate(images: List<Image>) {
                invalidateToolbar()
                if (!config.isMultipleMode && images.isNotEmpty()) {
                    onDone()
                }
            }
        })
        presenter = ImagePickerPresenter(ImageFileLoader(this))
        presenter!!.attachView(this)
    }

    private fun setupToolbar() {
        toolbar.config(config)
        toolbar.setOnBackClickListener(backClickListener)
        toolbar.setOnCameraClickListener(cameraClickListener)
        toolbar.setOnDoneClickListener(doneClickListener)
    }

    override fun onResume() {
        super.onResume()
        dataWithPermission
    }

    private fun setImageAdapter(images: List<Image>, title: String) {
        recyclerViewManager.setImageAdapter(images, title)
        invalidateToolbar()
    }

    private fun setFolderAdapter(folders: List<Folder>) {
        recyclerViewManager.setFolderAdapter(folders)
        invalidateToolbar()
    }

    private fun invalidateToolbar() {
        toolbar.setTitle(recyclerViewManager.getTitle())
        toolbar.showDoneButton(recyclerViewManager.isShowDoneButton)
    }

    private fun onDone() {
        presenter.onDoneSelectImages(recyclerViewManager.selectedImages)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recyclerViewManager.changeOrientation(newConfig.orientation)
    }

    private val dataWithPermission: Unit
        private get() {
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, object : PermissionAskListener {
                override fun onNeedPermission() {
                    requestAllPermissions(this@ImagePickerActivity, permissions, Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION)
                }

                override fun onPermissionPreviouslyDenied() {
                    requestAllPermissions(this@ImagePickerActivity, permissions, Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION)
                }

                override fun onPermissionDisabled() {
                    snackBar.show(R.string.imagepicker_msg_no_write_external_storage_permission, View.OnClickListener { openAppSettings(this@ImagePickerActivity) })
                }

                override fun onPermissionGranted() {
                    getData()
                }
            })
        }

    private fun getData() {
        presenter.abortLoading()
        presenter.loadImages(config!!.isFolderMode)
    }

    private fun captureImageWithPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        checkPermission(this, Manifest.permission.CAMERA, object : PermissionAskListener {
            override fun onNeedPermission() {
                requestAllPermissions(this@ImagePickerActivity, permissions, Config.RC_CAMERA_PERMISSION)
            }

            override fun onPermissionPreviouslyDenied() {
                requestAllPermissions(this@ImagePickerActivity, permissions, Config.RC_CAMERA_PERMISSION)
            }

            override fun onPermissionDisabled() {
                snackBar.show(R.string.imagepicker_msg_no_camera_permission, View.OnClickListener { openAppSettings(this@ImagePickerActivity) })
            }

            override fun onPermissionGranted() {
                captureImage()
            }
        })
    }

    private fun captureImage() {
        if (!checkCameraAvailability(this)) {
            return
        }
        presenter.captureImage(this, config, Config.RC_CAPTURE_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Config.RC_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            presenter.finishCaptureImage(this, data, config)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Config.RC_WRITE_EXTERNAL_STORAGE_PERMISSION -> {
                run {
                    if (hasGranted(grantResults)) {
                        logger?.d("Write External permission granted")
                        getData()
                        return
                    }
                    logger?.e("Permission not granted: results len = " + grantResults.size +
                            " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")
                    finish()
                }
                run {
                    if (hasGranted(grantResults)) {
                        logger?.d("Camera permission granted")
                        captureImage()
                        return
                    }
                    logger?.e("Permission not granted: results len = " + grantResults.size +
                            " Result code = " + if (grantResults.isNotEmpty()) grantResults[0] else "(empty)")
                }
            }
            Config.RC_CAMERA_PERMISSION -> {
                if (hasGranted(grantResults)) {
                    logger?.d("Camera permission granted")
                    captureImage()
                    return
                }
                logger?.e("Permission not granted: results len = " + grantResults.size +
                        " Result code = " + if (grantResults.isNotEmpty()) grantResults[0] else "(empty)")
            }
            else -> {
                logger?.d("Got unexpected permission result: $requestCode")
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (handler == null) {
            handler = Handler()
        }
        observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                dataWithPermission
            }
        }
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (presenter != null) {
            presenter.abortLoading()
            presenter.detachView()
        }
        if (observer != null) {
            contentResolver.unregisterContentObserver(observer!!)
            observer = null
        }
        if (handler != null) {
            handler!!.removeCallbacksAndMessages(null)
            handler = null
        }
    }

    override fun onBackPressed() {
        recyclerViewManager!!.handleBack(object : OnBackAction {
            override fun onBackToFolder() {
                invalidateToolbar()
            }

            override fun onFinishImagePicker() {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        })
    }

    /**
     * MVP view methods
     */
    override fun showLoading(isLoading: Boolean) {
        progressWheel!!.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView!!.visibility = if (isLoading) View.GONE else View.VISIBLE
        noImageText!!.visibility = View.GONE
    }

    override fun showFetchCompleted(images: List<Image>, folders: List<Folder>) {
        if (config!!.isFolderMode) {
            setFolderAdapter(folders)
        } else {
            setImageAdapter(images, config!!.imageTitle)
        }
        progressWheel!!.visibility = View.GONE
        recyclerView!!.visibility = View.VISIBLE
        noImageText!!.visibility = View.GONE
    }

    override fun showError(throwable: Throwable) {
        var message = getString(R.string.imagepicker_error_unknown)
        if (throwable != null && throwable is NullPointerException) {
            message = getString(R.string.imagepicker_error_images_not_exist)
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progressWheel!!.visibility = View.GONE
    }

    override fun showEmpty() {
        progressWheel!!.visibility = View.GONE
        recyclerView!!.visibility = View.GONE
        noImageText!!.visibility = View.VISIBLE
    }

    override fun showCapturedImage(images: List<Image>) {
        val shouldSelect = recyclerViewManager!!.selectImage()
        if (shouldSelect) {
            recyclerViewManager!!.addSelectedImages(images)
        }
        dataWithPermission
    }

    override fun finishPickImages(images: List<Image>) {
        val data = Intent()
        data.putParcelableArrayListExtra(Config.EXTRA_IMAGES, images as ArrayList<out Parcelable?>)
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}