package com.leodemo.genai_android.ui.screens.chatRoom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    val currentMessage by viewModel.newestMessage.collectAsStateWithLifecycle()
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
            currentMessage = currentMessage,
            historyMessage = historyMessage,
            onSend = viewModel::sendMessage
        )
    }
}

@Composable
fun ChatRoomContent(
    modifier: Modifier,
    currentMessage: ChatMessage,
    historyMessage: List<ChatMessage>,
    onSend: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ChatRoomChatArea(
            currentMessage = currentMessage,
            historyMessage = historyMessage,
        )
        ChatRoomInputField(onSend = onSend)
    }
}

@Composable
fun ColumnScope.ChatRoomChatArea(
    currentMessage: ChatMessage,
    historyMessage: List<ChatMessage>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .padding(10.dp),
        reverseLayout = true
    ) {
        item {
            ChatRoomChatItem(question = currentMessage.question, answer = currentMessage.answer)
        }
        items(historyMessage.reversed()) { message ->
            ChatRoomChatItem(question = message.question, answer = message.answer)
        }
    }
}

@Composable
fun ChatRoomChatItem(
    question: String,
    answer: String
) {
    ChatRoomAnswerBubble(answer = answer)
    UserMessageBubble(text = question)
}

@Composable
fun ChatRoomAnswerBubble(
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
fun ChatRoomInputField(
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
                onClick = onClick@{
                    if (textFieldValue.text.isBlank()) return@onClick
                    onSend(textFieldValue.text)
                    textFieldValue = TextFieldValue("")
                }
            )
        }
    )
}