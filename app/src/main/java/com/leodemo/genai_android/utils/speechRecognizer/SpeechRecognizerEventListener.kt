package com.leodemo.genai_android.utils.speechRecognizer

/**
 * A Basic speech action in [RecognitionListener]
 *
 * @property [onStart] for [RecognitionListener.onBeginningOfSpeech]
 * @property [onResult] for [RecognitionListener.onResults]
 * @property [onError] for [RecognitionListener.onError]
 *
 * @see android.speech.RecognitionListener
 * @see android.speech.RecognitionListener.onBeginningOfSpeech
 * @see android.speech.RecognitionListener.onResults
 * @see android.speech.RecognitionListener.onError
 * @sample BasicRecognitionListener
 * @sample SimpleRecognitionListener
 */
interface SpeechRecognizerEventListener {
    fun onStart()
    fun onResult(result: String?)
    fun onError(code: Int)
}