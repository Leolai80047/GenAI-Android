package com.leodemo.genai_android.ui.screens

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object MenuScreen : Screen()

    @Serializable
    data object SummarizeScreen : Screen()

    @Serializable
    data object PhotoDescribeScreen : Screen()

    @Serializable
    data object ChatRoomScreen : Screen()

    @Serializable
    data object CameraCaptureScreen : Screen()
}