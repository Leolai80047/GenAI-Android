package com.leodemo.genai_android.ui.screens.summarize

sealed class SummarizeAnswerUiState(val data: String) {
    class Loading(data: String) : SummarizeAnswerUiState(data)
    class Idle(data: String) : SummarizeAnswerUiState(data)
    data class Error(val message: String) : SummarizeAnswerUiState("")
}