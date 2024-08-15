package com.leodemo.genai_android.ui.screens.photoDescribe

sealed class PhotoDescribeUiState(val data: String) {
    class Loading(data: String) : PhotoDescribeUiState(data)
    class Idle(data: String) : PhotoDescribeUiState(data)
    data class Error(val message: String) : PhotoDescribeUiState("")
}