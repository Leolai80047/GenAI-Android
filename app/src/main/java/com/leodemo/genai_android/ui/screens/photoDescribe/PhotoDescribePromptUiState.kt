package com.leodemo.genai_android.ui.screens.photoDescribe

import android.net.Uri

data class PhotoDescribePromptUiState(
    val question: String = "",
    val uri: Uri? = null,
)