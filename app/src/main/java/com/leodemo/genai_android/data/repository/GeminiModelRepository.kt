package com.leodemo.genai_android.data.repository

import android.graphics.Bitmap
import com.leodemo.genai_android.data.model.AiModel
import com.leodemo.genai_android.domain.repository.AiModelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeminiModelRepository @Inject constructor(
    private val model: AiModel
) : AiModelRepository {
    override fun fetchTextGenerationStream(prompt: String): Flow<String> {
        return model.generateTextByTextStream(prompt)
    }

    override fun fetchTextImageGenerationStream(
        prompt: String,
        bitmap: Bitmap
    ): Flow<String> {
        return model.generateTextByTextAndImageStream(prompt, bitmap)
    }

    override fun fetchChatAnswerStream(prompt: String): Flow<String> {
        return model.generateChatStream(prompt)
    }
}