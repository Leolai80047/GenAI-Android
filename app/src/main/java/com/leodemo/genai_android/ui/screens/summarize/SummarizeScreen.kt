package com.leodemo.genai_android.ui.screens.summarize

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.utils.SpeechRecognizerManager
import java.util.Locale


@Composable
fun SummarizeScreen(
    viewModel: SummarizeViewModel = hiltViewModel()
) {
    val answer by viewModel.answer.observeAsState("")
    var inputText by remember {
        mutableStateOf("")
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            GenAITopAppBar(stringResource(R.string.app_name))
        },
        content = { paddingValues ->
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                SummarizeAnswerArea(answer = answer)
                Row(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                ) {
                    SummarizeInputField(
                        inputText = inputText,
                        setInputText = {
                            inputText = it
                        }
                    )
                    SummarizeSendMessageButton(
                        sendMessage = sendMessage@{
                            if (inputText.isBlank()) return@sendMessage
                            viewModel.answer.value = ""
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        },
                    )
                }

            }
        }
    )
}

@Composable
private fun ColumnScope.SummarizeAnswerArea(answer: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .weight(1f)
            .padding(10.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (answer.isNotEmpty()) {
                SummarizeText(answer)
            }
        }

    }
}

@Composable
private fun SummarizeText(description: String) {
    Text(
        modifier = Modifier.padding(10.dp),
        text = AnnotatedString.fromHtml(description),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun RowScope.SummarizeInputField(
    inputText: String,
    setInputText: (String) -> Unit
) {
    val textFieldValueState by remember(inputText) {
        mutableStateOf(
            TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length)
            )
        )
    }
    TextField(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
        value = textFieldValueState,
        onValueChange = {
            setInputText(it.text)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        trailingIcon = {
            SpeechRecognizerButton(
                onResult = { result ->
                    setInputText(result)
                }
            )
        }
    )
}

@Composable
private fun SummarizeSendMessageButton(sendMessage: () -> Unit) {
    IconButton(
        modifier = Modifier
            .fillMaxHeight()
            .width(70.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        onClick = {
            sendMessage()
        }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Send,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null
        )
    }
}

@Composable
private fun SpeechRecognizerButton(
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

    SpeechRecognizerIcon(onClick = {
        startVoice(context)
    })
}

@Composable
private fun SpeechRecognizerIcon(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.ic_mic),
            contentDescription = null
        )
    }
}