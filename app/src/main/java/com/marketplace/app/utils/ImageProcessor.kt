package com.marketplace.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import com.marketplace.app.domain.model.ImageData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

class ImageProcessor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun processImage(uri: Uri): ImageData = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        processBitmap(bitmap)
    }

    suspend fun processBitmap(bitmap: Bitmap): ImageData = withContext(Dispatchers.IO) {
        // Resize image to reduce memory usage
        val resized = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)

        // Generate thumbnail
        val thumbnail = Bitmap.createScaledBitmap(bitmap, 150, 150, true)

        // Save to cache directory
        val imageFile = File(context.cacheDir, "images/${UUID.randomUUID()}.jpg")
        imageFile.parentFile?.mkdirs()

        FileOutputStream(imageFile).use { out ->
            resized.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }

        val thumbnailFile = File(context.cacheDir, "thumbnails/${UUID.randomUUID()}.jpg")
        thumbnailFile.parentFile?.mkdirs()

        FileOutputStream(thumbnailFile).use { out ->
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, out)
        }

        resized.recycle()
        thumbnail.recycle()
        bitmap.recycle()

        ImageData(
            url = imageFile.absolutePath,
            thumbnailUrl = thumbnailFile.absolutePath,
            uri = imageFile.toUri()
        )
    }
}
