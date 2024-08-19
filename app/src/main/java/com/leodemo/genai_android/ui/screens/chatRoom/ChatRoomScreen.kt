package com.leodemo.genai_android.ui.screens.chatRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.ChatTextField
import com.leodemo.genai_android.ui.component.DefaultChatTextFieldActionButton
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.ui.component.StyledAnswerText
import com.leodemo.genai_android.ui.component.UserMessageBubble

@Composable
fun ChatRoomScreen(
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val messageUiState by viewModel.messageUiState.collectAsStateWithLifecycle()
    val historyMessage by viewModel.historyMessage.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            GenAITopAppBar(title = stringResource(R.string.app_name))
        }
    ) { paddingValues ->
        ChatRoomContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            messageUiState = messageUiState,
            historyMessage = historyMessage,
            onSend = viewModel::sendMessage
        )
    }
}

@Composable
private fun ChatRoomContent(
    modifier: Modifier,
    messageUiState: ChatRoomMessageUiState,
    historyMessage: List<ChatMessage>,
    onSend: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ChatRoomMessageStateView(messageUiState = messageUiState)
        ChatRoomChatArea(
            currentMessage = messageUiState.data,
            historyMessage = historyMessage,
        )
        ChatRoomInputField(
            messageUiState = messageUiState,
            onSend = onSend
        )
    }
}

@Composable
private fun ColumnScope.ChatRoomChatArea(
    currentMessage: ChatMessage,
    historyMessage: List<ChatMessage>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f),
        contentPadding = PaddingValues(10.dp),
        reverseLayout = true
    ) {
        item {
            ChatRoomChatItem(question = currentMessage.prompt, answer = currentMessage.answer)
        }
        items(historyMessage.reversed()) { message ->
            ChatRoomChatItem(question = message.prompt, answer = message.answer)
        }
    }
}

@Composable
private fun ChatRoomChatItem(
    question: String,
    answer: String
) {
    ChatRoomAnswerBubble(answer = answer)
    UserMessageBubble(text = question)
}

@Composable
private fun ChatRoomAnswerBubble(
    answer: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        if (answer.isNotEmpty()) {
            StyledAnswerText(
                modifier = Modifier.padding(10.dp),
                text = answer,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ChatRoomInputField(
    messageUiState: ChatRoomMessageUiState,
    onSend: (String) -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    ChatTextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
        },
        actionButton = {
            DefaultChatTextFieldActionButton(
                enable = messageUiState !is ChatRoomMessageUiState.Loading,
                onClick = onClick@{
                    if (textFieldValue.text.isBlank()) return@onClick
                    onSend(textFieldValue.text)
                    textFieldValue = TextFieldValue("")
                }
            )
        }
    )
}

@Composable
private fun ChatRoomMessageStateView(
    messageUiState: ChatRoomMessageUiState
) {
    when (messageUiState) {
        is ChatRoomMessageUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is ChatRoomMessageUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.error)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Something went error!",
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }

        is ChatRoomMessageUiState.Idle -> Unit
    }
}