package com.leodemo.genai_android.ui.screens.chatRoom

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
class ChatRoomViewModel @Inject constructor(
    private val aiModelRepository: AiModelRepository
) : ViewModel() {

    private val _historyMessage = MutableStateFlow(emptyList<ChatMessage>())
    val historyMessage = _historyMessage.asStateFlow()

    private val _messageUiState: MutableStateFlow<ChatRoomMessageUiState> =
        MutableStateFlow(ChatRoomMessageUiState.Idle())
    val messageUiState = _messageUiState.asStateFlow()

    fun sendMessage(prompt: String) {
        viewModelScope.launch {
            aiModelRepository.fetchChatAnswerStream(prompt)
                .onStart {
                    val chatMessage = ChatMessage(prompt = prompt, answer = "")
                    _messageUiState.value = ChatRoomMessageUiState.Loading(chatMessage)
                }.catch {
                    FirebaseCrashlytics.getInstance().apply {
                        log("Prompt: $prompt")
                        recordException(it)
                    }
                    _messageUiState.value = ChatRoomMessageUiState.Error("GenerateText error!")
                }
                .onCompletion {
                    val chatMessage = _messageUiState.value.data
                    val answer = chatMessage.answer.markdownToHtml()
                    _historyMessage.value += chatMessage.copy(answer = answer)
                    _messageUiState.value = ChatRoomMessageUiState.Idle()
                }
                .collect {
                    val chatMessage = _messageUiState.value.data
                    val partAnswer = "${chatMessage.answer}$it"
                    _messageUiState.value =
                        ChatRoomMessageUiState.Loading(chatMessage.copy(answer = partAnswer))
                }
        }
    }
}