package com.leodemo.genai_android.ui.screens.chatRoom

data class ChatMessage(
    val question: String,
    val answer: String
) {
    companion object {
        val EMPTY = ChatMessage("", "")
    }
}