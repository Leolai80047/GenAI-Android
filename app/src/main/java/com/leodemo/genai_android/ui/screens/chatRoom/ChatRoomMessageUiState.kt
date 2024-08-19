package com.leodemo.genai_android.ui.screens.chatRoom

sealed class ChatRoomMessageUiState(val data: ChatMessage) {
    class Loading(data: ChatMessage) : ChatRoomMessageUiState(data)
    class Idle(data: ChatMessage = ChatMessage.EMPTY) : ChatRoomMessageUiState(data)
    data class Error(val message: String) : ChatRoomMessageUiState(ChatMessage.ERROR)
}