package com.leodemo.genai_android.ui.screens.chatRoom

data class ChatMessage(
    val prompt: String,
    val answer: String,
    val isError: Boolean = false,
) {
    companion object {
        val EMPTY = ChatMessage("", "")
        val ERROR = ChatMessage("", "", isError = true)
    }
}