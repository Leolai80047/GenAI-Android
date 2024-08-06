package com.leodemo.chat_room.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.util.Locale

class SpeechRecognizerManager(
    activity: ComponentActivity,
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
    private val permissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        for ((key, isGranted) in it) {
            if (!isGranted) {
                Toast.makeText(activity, "You need $key permission!", Toast.LENGTH_LONG)
                    .show()
                return@registerForActivityResult
            }
        }
        initConfig(activity)
        speechRecognizer?.startListening(speechIntent)
    }

    fun startVoice(locale: Locale = this.locale) {
        this.locale = locale
        stopVoice()
        checkPermission()
    }

    fun stopVoice() {
        speechRecognizer?.stopListening()
    }

    fun releaseSpeechRecognizer() {
        stopVoice()
        speechRecognizer?.destroy()
        speechRecognizer = null
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

    private fun checkPermission() {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.RECORD_AUDIO)
        )
    }
}