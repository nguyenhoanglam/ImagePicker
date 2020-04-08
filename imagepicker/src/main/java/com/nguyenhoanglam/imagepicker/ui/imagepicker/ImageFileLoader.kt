package com.nguyenhoanglam.imagepicker.ui.imagepicker

import android.content.Context
import android.provider.MediaStore
import com.nguyenhoanglam.imagepicker.listener.OnImageLoaderListener
import com.nguyenhoanglam.imagepicker.model.Folder
import com.nguyenhoanglam.imagepicker.model.Image
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by hoanglam on 8/17/17.
 */
class ImageFileLoader(private val context: Context) {

    private val projection = arrayOf(MediaStore.Images.Media._ID
            , MediaStore.Images.Media.DISPLAY_NAME
            , MediaStore.Images.Media.DATA
            , MediaStore.Images.Media.BUCKET_ID
            , MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

    private var executorService: ExecutorService? = null

    fun loadDeviceImages(isFolderMode: Boolean, listener: OnImageLoaderListener) {
        getExecutorService()!!.execute(ImageLoadRunnable(isFolderMode, listener))
    }

    fun abortLoadImages() {
        if (executorService != null) {
            executorService!!.shutdown()
            executorService = null
        }
    }

    private fun getExecutorService(): ExecutorService? {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor()
        }
        return executorService
    }

    private inner class ImageLoadRunnable(private val isFolderMode: Boolean, private val listener: OnImageLoaderListener) : Runnable {
        override fun run() {
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_ADDED)
            if (cursor == null) {
                listener.onFailed(NullPointerException())
                return
            }
            val images: MutableList<Image> = ArrayList(cursor.count)
            val folderMap: MutableMap<String, Folder>? = if (isFolderMode) LinkedHashMap() else null
            if (cursor.moveToLast()) {
                do {
                    val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    val name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val bucketId = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID))
                    val bucket = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    val file = makeSafeFile(path)
                    if (file != null && file.exists()) {
                        val image = Image(id, name, path)
                        images.add(image)
                        if (folderMap != null) {
                            var folder = folderMap[bucket]
                            if (folder == null) {
                                folder = Folder(bucket)
                                folderMap[bucket] = folder
                            }
                            folder.images.add(image)
                        }
                    }
                } while (cursor.moveToPrevious())
            }
            cursor.close()

            /* Convert HashMap to ArrayList if not null */
            var folders: List<Folder> = arrayListOf()
            if (folderMap != null) {
                folders = ArrayList(folderMap.values)
            }
            listener.onImageLoaded(images, folders)
        }

    }

    companion object {
        private fun makeSafeFile(path: String?): File? {
            return if (path == null || path.isEmpty()) {
                null
            } else try {
                File(path)
            } catch (ignored: Exception) {
                null
            }
        }
    }

}