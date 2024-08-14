package com.leodemo.genai_android.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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