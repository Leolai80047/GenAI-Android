package com.leodemo.genai_android.ui.screens.summarize

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.leodemo.genai_android.domain.repository.AiModelRepository
import com.leodemo.genai_android.utils.extensions.markdownToHtml
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummarizeViewModel @Inject constructor(
    private val aiModelRepository: AiModelRepository
) : ViewModel() {

    val answer = MutableLiveData("")

    fun sendMessage(message: String) {
        viewModelScope.launch {
            aiModelRepository.fetchTextGenerationStream(message)
                .catch {
                    FirebaseCrashlytics.getInstance().log("Prompt: $message")
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
                .onCompletion {
                    answer.value = answer.value?.markdownToHtml() ?: return@onCompletion
                }
                .collect {
                    answer.value += it
                }
        }
    }
}