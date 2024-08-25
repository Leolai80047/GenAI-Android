package com.leodemo.genai_android.utils.speechRecognizer

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer

class BasicRecognitionListener(
    private val speechRecognizerEventListener: SpeechRecognizerEventListener
) : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onBeginningOfSpeech() {
        speechRecognizerEventListener.onStart()
    }

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {}

    override fun onError(error: Int) {
        speechRecognizerEventListener.onError(error)
    }

    override fun onResults(results: Bundle?) {
        results?.let {
            val matched = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matched?.firstOrNull()
            speechRecognizerEventListener.onResult(text)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}
}