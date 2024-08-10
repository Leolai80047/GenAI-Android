package com.leodemo.genai_android.ui.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.leodemo.genai_android.R
import com.leodemo.genai_android.utils.SpeechRecognizerManager
import java.util.Locale

@Composable
fun SpeechRecognizerButton(
    content: @Composable ((() -> Unit) -> Unit)? = null,
    onResult: (String) -> Unit
) {
    val context = LocalContext.current
    val speechListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {}
        override fun onResults(results: Bundle?) {
            results?.let {
                val matched = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matched?.firstOrNull()
                if (text == null) {
                    Toast.makeText(context, "Unable to recognize", Toast.LENGTH_LONG)
                        .show()
                } else {
                    onResult(text)
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
    val speechRecognizerManager = remember {
        SpeechRecognizerManager(
            context,
            speechListener
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "You need permission to start speech recognizer",
                Toast.LENGTH_LONG
            ).show()
        } else {
            speechRecognizerManager.startVoice()
        }
    }

    fun startVoice(context: Context, locale: Locale? = null) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            speechRecognizerManager.startVoice(locale)
        } else {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerManager.releaseSpeechRecognizer()
        }
    }

    val onClick = {
        startVoice(context)
    }
    if (content == null) {
        DefaultSpeechRecognizerIcon(onClick = onClick)
    } else {
        content(onClick)
    }
}

@Composable
private fun DefaultSpeechRecognizerIcon(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_mic),
            contentDescription = null
        )
    }
}