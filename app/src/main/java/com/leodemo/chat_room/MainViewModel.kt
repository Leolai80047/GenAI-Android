package com.leodemo.chat_room

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val model = GenerativeModel(
        "gemini-1.5-flash",
        BuildConfig.api_key
    )

    val text = MutableLiveData("")

    fun sendMessage(message: String) {
        viewModelScope.launch {
            model.generateContentStream(message).collect{
                text.value+= it.text?: ""
            }
        }
    }
}