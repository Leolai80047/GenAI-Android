package com.leodemo.genai_android.ui.screens.photoDescribe

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.leodemo.genai_android.domain.repository.AiModelRepository
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
class PhotoDescribeViewModel @Inject constructor(
    private val aiModelRepository: AiModelRepository
) : ViewModel() {

    private val _answerUiState =
        MutableStateFlow<PhotoDescribeAnswerUiState>(PhotoDescribeAnswerUiState.Idle(""))
    val answerUiState = _answerUiState.asStateFlow()

    private val _promptUiState = MutableStateFlow(PhotoDescribePromptUiState())
    val promptUiState = _promptUiState.asStateFlow()

    fun send(bitmap: Bitmap, prompt: String) {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            aiModelRepository.fetchTextImageGenerationStream(prompt, bitmap)
                .onStart {
                    _answerUiState.value = PhotoDescribeAnswerUiState.Loading("")
                    _promptUiState.value = _promptUiState.value.copy(question = prompt)
                }
                .catch {
                    FirebaseCrashlytics.getInstance().apply {
                        log("Prompt: $prompt")
                        log("Bitmap width: ${bitmap.width}, height: ${bitmap.height}, config: ${bitmap.config}")
                        recordException(it)
                    }
                    _answerUiState.value = PhotoDescribeAnswerUiState.Error("")
                }
                .onCompletion { exception ->
                    if (_answerUiState.value !is PhotoDescribeAnswerUiState.Error) {
                        val answer = _answerUiState.value.data.markdownToHtml()
                        _answerUiState.value = PhotoDescribeAnswerUiState.Idle(answer)
                    }
                }
                .collect {
                    val partAnswer = "${_answerUiState.value.data}${it}"
                    _answerUiState.value = PhotoDescribeAnswerUiState.Loading(partAnswer)
                }
        }
    }

    fun setSelectedUri(uri: Uri?) {
        _answerUiState.value = PhotoDescribeAnswerUiState.Idle("")
        _promptUiState.value = _promptUiState.value.copy(uri = uri)
    }
}