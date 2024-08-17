package com.leodemo.genai_android.data.local.ai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.leodemo.genai_android.data.model.AiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GeminiModel @Inject constructor(
    private val model: GenerativeModel
) : AiModel() {

    override fun generateTextByTextStream(prompt: String): Flow<String> {
        val content = content {
            text(prompt)
        }
        return model.generateContentStream(content).map {
            it.text ?: ""
        }
    }

    override fun generateTextByTextAndImageStream(
        prompt: String,
        bitmap: Bitmap
    ): Flow<String> {
        val content = content {
            text(prompt)
            image(bitmap)
        }
        return model.generateContentStream(content).map {
            it.text ?: ""
        }
    }
}