package com.leodemo.genai_android.ui.screens.chatRoom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leodemo.genai_android.domain.repository.AiModelRepository
import com.leodemo.genai_android.utils.extensions.markdownToHtml
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _newestMessage = MutableStateFlow(ChatMessage.EMPTY)
    val newestMessage = _newestMessage.asStateFlow()

    fun sendMessage(question: String) {
        viewModelScope.launch {
            aiModelRepository.fetchChatAnswerStream(question)
                .onStart {
                    _newestMessage.value = ChatMessage(question = question, answer = "")
                }
                .onCompletion {
                    val answer = _newestMessage.value.answer.markdownToHtml()
                    _historyMessage.value += newestMessage.value.copy(answer = answer)
                    _newestMessage.value = ChatMessage.EMPTY
                }
                .collect {
                    val partAnswer = "${_newestMessage.value.answer}$it"
                    _newestMessage.value = _newestMessage.value.copy(answer = partAnswer)
                }
        }
    }
}