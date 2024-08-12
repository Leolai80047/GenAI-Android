package com.leodemo.genai_android.ui.screens.summarize

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.leodemo.genai_android.BuildConfig
import com.leodemo.genai_android.utils.markdownToHtml
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummarizeViewModel @Inject constructor() : ViewModel() {
    private val model = GenerativeModel(
        "gemini-1.5-flash",
        BuildConfig.api_key,
        generationConfig = generationConfig {
            temperature = 0.5f
        },
        safetySettings = listOf(
            SafetySetting(
                HarmCategory.DANGEROUS_CONTENT,
                BlockThreshold.NONE
            )
        )
    )

    val answer = MutableLiveData("")

    fun sendMessage(message: String) {
        viewModelScope.launch {
            model.generateContentStream(message)
                .catch {
                    FirebaseCrashlytics.getInstance().log("Prompt: $message")
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
                .onCompletion {
                    answer.value = answer.value?.markdownToHtml() ?: return@onCompletion
                }
                .collect {
                    answer.value += it.text ?: ""
                }
        }
    }
}