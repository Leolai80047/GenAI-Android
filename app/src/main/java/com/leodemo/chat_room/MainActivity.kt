package com.leodemo.chat_room

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.leodemo.chat_room.ui.theme.ChatRoomTheme
import com.leodemo.chat_room.utils.SpeechRecognizerManager

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var speechRecognizerManager: SpeechRecognizerManager
    private val speechListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {

        }

        override fun onBeginningOfSpeech() {

        }

        override fun onRmsChanged(rmsdB: Float) {

        }

        override fun onBufferReceived(buffer: ByteArray?) {

        }

        override fun onEndOfSpeech() {

        }

        override fun onError(error: Int) {

        }

        override fun onResults(results: Bundle?) {
            results?.let {
                val matched = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matched?.firstOrNull()
                if (text == null) {
                    Toast.makeText(this@MainActivity, "Unable to recognize", Toast.LENGTH_LONG)
                        .show()
                } else {
                    viewModel.inputText.value += text
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {

        }

        override fun onEvent(eventType: Int, params: Bundle?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speechRecognizerManager = SpeechRecognizerManager(this, speechListener)
        enableEdgeToEdge()
        setContent {
            ChatRoomTheme {
                ChatRoom(
                    viewModel = viewModel,
                    startVoice = speechRecognizerManager::startVoice,
                )
            }
        }
    }

    override fun onDestroy() {
        speechRecognizerManager.releaseSpeechRecognizer()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatRoom(
    viewModel: MainViewModel,
    startVoice: () -> Unit,
) {
    val answer by viewModel.answer.observeAsState("")
    val inputText by viewModel.inputText.observeAsState("")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat Room",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            )
        },
        content = { paddingValues ->
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                ChatRoomAnswerArea(answer = answer)
                ChatRoomInputField(
                    sendMessage = { questionMessage ->
                        viewModel.sendMessage(questionMessage)
                        viewModel.answer.value = ""
                    },
                    inputText = inputText,
                    setInputText = {
                        viewModel.inputText.value = it
                    },
                    startVoice = startVoice
                )
            }
        }
    )
}

@Composable
private fun ColumnScope.ChatRoomAnswerArea(answer: String) {
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
                ChatRoomText(answer)
            }
        }

    }
}

@Composable
private fun ChatRoomText(description: String) {
    Text(
        modifier = Modifier.padding(10.dp),
        text = AnnotatedString.fromHtml(description),
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun ChatRoomInputField(
    inputText: String,
    setInputText: (String) -> Unit,
    sendMessage: (String) -> Unit,
    startVoice: () -> Unit,
) {
    val textFieldValueState by remember(inputText) {
        mutableStateOf(
            TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length)
            )
        )
    }
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
    ) {
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
                IconButton(onClick = {
                    startVoice()
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic),
                        contentDescription = null
                    )
                }
            }
        )
        IconButton(
            modifier = Modifier
                .fillMaxHeight()
                .width(70.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            onClick = onClick@{
                if (inputText.isBlank()) return@onClick
                sendMessage(inputText)
                setInputText("")
            }
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null
            )
        }
    }
}