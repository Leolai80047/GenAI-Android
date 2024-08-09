package com.leodemo.genai_android.ui.screens.summarize

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.leodemo.genai_android.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
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
                .onCompletion {
                    answer.value = markdown2html(answer.value ?: return@onCompletion)
                }
                .collect {
                    answer.value += it.text ?: ""
                }
        }
    }

    private fun markdown2html(markdownText: String): String {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(markdownText)
        val html = HtmlGenerator(markdownText, parsedTree, flavour).generateHtml()
        return html
    }
}