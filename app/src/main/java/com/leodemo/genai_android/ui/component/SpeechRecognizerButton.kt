package com.leodemo.genai_android.ui.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.leodemo.genai_android.R
import com.leodemo.genai_android.utils.TestTags
import com.leodemo.genai_android.utils.speechRecognizer.SpeechRecognizerManager
import java.util.Locale

@Composable
fun SpeechRecognizerButton(
    speechRecognizerManager: SpeechRecognizerManager,
    content: @Composable ((() -> Unit) -> Unit)? = null,
) {
    val context = LocalContext.current
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
    IconButton(
        modifier = Modifier
            .size(30.dp)
            .testTag(TestTags.SPEECH_RECOGNIZER_BUTTON),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_mic),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}