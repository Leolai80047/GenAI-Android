package com.leodemo.genai_android.ui.screens.photoDescribe

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.leodemo.genai_android.BuildConfig
import com.leodemo.genai_android.utils.extensions.markdownToHtml
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    val answer = MutableStateFlow("")

    fun send(bitmap: Bitmap, prompt: String) {
        if (prompt.isBlank()) return
        answer.value = ""
        val content = content {
            image(bitmap)
            text(prompt)
        }
        viewModelScope.launch {
            model.generateContentStream(content)
                .catch {
                    FirebaseCrashlytics.getInstance().apply {
                        log("Prompt: $prompt")
                        log("Bitmap width: ${bitmap.width}, height: ${bitmap.height}, config: ${bitmap.config}")
                        recordException(it)
                    }
                }
                .onCompletion {
                    answer.value = answer.value?.markdownToHtml() ?: return@onCompletion
                }
                .collect {
                    answer.value += it.text
                }
        }
    }

    fun setSelectedUri(uri: Uri?) {
        answer.value = ""
        _imageUri.value = uri
    }
}