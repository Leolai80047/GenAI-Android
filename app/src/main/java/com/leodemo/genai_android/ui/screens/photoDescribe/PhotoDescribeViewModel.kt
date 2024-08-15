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
import kotlinx.coroutines.flow.onStart
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

    private val _answerState = MutableStateFlow<PhotoDescribeUiState>(PhotoDescribeUiState.Idle(""))
    val answerState = _answerState.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    fun send(bitmap: Bitmap, prompt: String) {
        if (prompt.isBlank()) return
        val content = content {
            image(bitmap)
            text(prompt)
        }
        viewModelScope.launch {
            model.generateContentStream(content)
                .onStart {
                    _answerState.value = PhotoDescribeUiState.Loading("")
                }
                .catch {
                    FirebaseCrashlytics.getInstance().apply {
                        log("Prompt: $prompt")
                        log("Bitmap width: ${bitmap.width}, height: ${bitmap.height}, config: ${bitmap.config}")
                        recordException(it)
                    }
                    _answerState.value = PhotoDescribeUiState.Error("")
                }
                .onCompletion { exception ->
                    if (_answerState.value !is PhotoDescribeUiState.Error) {
                        val answer = _answerState.value.data.markdownToHtml()
                        _answerState.value = PhotoDescribeUiState.Idle(answer)
                    }
                }
                .collect {
                    val partAnswer = "${_answerState.value.data}${it.text}"
                    _answerState.value = PhotoDescribeUiState.Loading(partAnswer)
                }
        }
    }

    fun setSelectedUri(uri: Uri?) {
        _answerState.value = PhotoDescribeUiState.Idle("")
        _imageUri.value = uri
    }
}