package com.leodemo.genai_android.utils

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import java.util.Locale

class SpeechRecognizerManager(
    private val context: Context,
    private val recognizerListener: RecognitionListener
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var locale = Locale.getDefault()
    private val lang
        get() = locale.toString()
    private val speechIntent
        get() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, lang)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, lang)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }

    private fun initConfig(context: Context) {
        if (speechRecognizer != null) return
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "Voice input not support!", Toast.LENGTH_LONG)
                .show()
        } else {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(recognizerListener)
        }
    }

    fun startVoice(locale: Locale? = this.locale) {
        locale?.let {
            this.locale = it
        }
        stopVoice()
        initConfig(context)
        speechRecognizer?.startListening(speechIntent)
    }

    fun stopVoice() {
        speechRecognizer?.cancel()
    }

    fun releaseSpeechRecognizer() {
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}