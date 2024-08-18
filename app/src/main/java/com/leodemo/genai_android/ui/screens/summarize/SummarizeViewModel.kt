package com.leodemo.genai_android.ui.screens.summarize

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
class SummarizeViewModel @Inject constructor(
    private val aiModelRepository: AiModelRepository
) : ViewModel() {

    private val _answerUiState =
        MutableStateFlow<SummarizeAnswerUiState>(SummarizeAnswerUiState.Idle(""))
    val answerUiState = _answerUiState.asStateFlow()

    private val _question = MutableStateFlow("")
    val question = _question.asStateFlow()

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            aiModelRepository.fetchTextGenerationStream(message)
                .onStart {
                    _answerUiState.value = SummarizeAnswerUiState.Loading("")
                    _question.value = message
                }
                .catch {
                    FirebaseCrashlytics.getInstance().log("Prompt: $message")
                    FirebaseCrashlytics.getInstance().recordException(it)
                    _answerUiState.value = SummarizeAnswerUiState.Error("GenerateText error!")
                }
                .onCompletion {
                    if (_answerUiState.value !is SummarizeAnswerUiState.Error) {
                        val answer = _answerUiState.value.data.markdownToHtml()
                        _answerUiState.value = SummarizeAnswerUiState.Idle(answer)
                    }
                }
                .collect {
                    val partAnswer = "${_answerUiState.value.data}$it"
                    _answerUiState.value = SummarizeAnswerUiState.Loading(partAnswer)
                }
        }
    }
}