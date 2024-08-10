package com.leodemo.genai_android.ui.screens.PhotoDescribeScreen

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.leodemo.genai_android.BuildConfig
import com.leodemo.genai_android.utils.markdownToHtml
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDescribeViewModel @Inject constructor() : ViewModel() {
    private val model = GenerativeModel(
        "gemini-1.5-flash",
        BuildConfig.api_key,
        generationConfig = generationConfig {
            temperature = 0.5f
        },
        safetySettings = listOf(
            SafetySetting(
                HarmCategory.DANGEROUS_CONTENT,
                BlockThreshold.NONE
            )
        )
    )

    val answer = MutableLiveData("")

    fun send(bitmap: Bitmap, prompt: String) {
        if (prompt.isBlank()) return
        answer.value = ""
        val content = content {
            image(bitmap)
            text(prompt)
        }
        viewModelScope.launch {
            model.generateContentStream(content)
                .onCompletion {
                    answer.value = answer.value?.markdownToHtml() ?: return@onCompletion
                }
                .collect {
                    answer.value += it.text
                }
        }
    }
}