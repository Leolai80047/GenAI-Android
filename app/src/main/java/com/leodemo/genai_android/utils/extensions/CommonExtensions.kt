package com.leodemo.genai_android.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun Bitmap.saveToUri(context: Context, fileName: String): Uri {
    return withContext(Dispatchers.IO) {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, fileName)
        FileOutputStream(file).use { outputStream ->
            compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        Uri.fromFile(file)
    }
}

fun Uri?.toBitmap(context: Context): Bitmap? {

    fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        var sampleSize = 1
        val (width: Int, height: Int) = options.run {
            outWidth to outHeight
        }

        if (width > reqWidth || height > reqHeight) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while ((halfWidth / sampleSize) >= reqWidth || (halfHeight / sampleSize) >= reqHeight) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }

    if (this == null || this == Uri.EMPTY) {
        Toast.makeText(context, "Image not set!", Toast.LENGTH_LONG).show()
        return null
    }
    return try {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(this)
        var bitmap: Bitmap? = null
        inputStream?.use {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            options.apply {
                val size = 768
                inSampleSize = calculateSampleSize(options, size, size)
                inJustDecodeBounds = false
            }

            bitmap = BitmapFactory.decodeStream(inputStream, null, options)
        }
        bitmap
    } catch (e: Exception) {
        Toast.makeText(context, "Bitmap convert error!", Toast.LENGTH_LONG).show()
        null
    }
}