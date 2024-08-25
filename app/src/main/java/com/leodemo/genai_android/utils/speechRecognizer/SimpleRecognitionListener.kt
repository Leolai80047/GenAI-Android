package com.leodemo.genai_android.utils.speechRecognizer

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast

class SimpleRecognitionListener(
    private val context: Context,
    private val onSpeechResult: (String) -> Unit
) : RecognitionListener {
    private val eventListener = object : SpeechRecognizerEventListener {
        override fun onStart() {

        }

        override fun onResult(result: String?) {
            onSpeechResult(result ?: "")
        }

        override fun onError(code: Int) {

        }

    }

    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {
        eventListener.onStart()
    }

    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {
        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG)
            .show()
        eventListener.onError(error)
    }

    override fun onResults(results: Bundle?) {
        results?.let {
            val matched = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matched?.firstOrNull()
            if (text == null) {
                Toast.makeText(context, "Unable to recognize", Toast.LENGTH_LONG)
                    .show()
            } else {
                eventListener.onResult(text)
            }
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
}