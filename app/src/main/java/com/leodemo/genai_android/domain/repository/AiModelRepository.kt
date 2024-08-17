package com.leodemo.genai_android.domain.repository

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface AiModelRepository {
    fun fetchTextGenerationStream(prompt: String): Flow<String>
    fun fetchTextImageGenerationStream(prompt: String, bitmap: Bitmap): Flow<String>
}