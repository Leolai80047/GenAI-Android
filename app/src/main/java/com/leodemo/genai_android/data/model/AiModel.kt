package com.leodemo.genai_android.data.model

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

abstract class AiModel {
    abstract fun generateTextByTextStream(prompt: String): Flow<String>
    abstract fun generateTextByTextAndImageStream(prompt: String, bitmap: Bitmap): Flow<String>
}