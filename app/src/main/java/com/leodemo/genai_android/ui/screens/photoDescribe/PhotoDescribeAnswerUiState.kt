package com.leodemo.genai_android.ui.screens.photoDescribe

sealed class PhotoDescribeAnswerUiState(val data: String) {
    class Loading(data: String) : PhotoDescribeAnswerUiState(data)
    class Idle(data: String) : PhotoDescribeAnswerUiState(data)
    data class Error(val message: String) : PhotoDescribeAnswerUiState("")
}