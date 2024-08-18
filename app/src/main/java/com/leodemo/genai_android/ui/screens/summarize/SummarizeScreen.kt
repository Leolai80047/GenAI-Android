package com.leodemo.genai_android.ui.screens.summarize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun SummarizeScreen(
    viewModel: SummarizeViewModel = hiltViewModel()
) {
    val answerUiState by viewModel.answerUiState.collectAsStateWithLifecycle()
    val question by viewModel.question.collectAsStateWithLifecycle("")

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
                SummarizeChatArea(
                    question = question,
                    answerUiState = answerUiState
                )
                SummarizeInputField(
                    answerUiState = answerUiState,
                    onSend = viewModel::sendMessage
                )
            }
        }
    )
}

@Composable
private fun ColumnScope.SummarizeChatArea(
    question: String,
    answerUiState: SummarizeAnswerUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .weight(1f)
            .padding(10.dp)
    ) {
        SummarizeAnswerStateView(answerUiState = answerUiState)
        UserMessageBubble(text = question)
        SummarizeAnswerArea(answer = answerUiState.data)
    }
}

@Composable
private fun SummarizeAnswerArea(
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
            SummarizeText(answer)
        }
    }
}

@Composable
private fun SummarizeText(description: String) {
    StyledAnswerText(
        modifier = Modifier.padding(10.dp),
        text = description,
        color = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
private fun SummarizeInputField(
    answerUiState: SummarizeAnswerUiState,
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
                enable = answerUiState !is SummarizeAnswerUiState.Loading,
                onClick = onClick@{
                    if (textFieldValue.text.isBlank()) return@onClick
                    onSend(textFieldValue.text)
                    textFieldValue = TextFieldValue("")
                },
            )
        }
    )
}

@Composable
private fun SummarizeAnswerStateView(
    answerUiState: SummarizeAnswerUiState
) {
    when (answerUiState) {
        is SummarizeAnswerUiState.Loading -> {
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

        is SummarizeAnswerUiState.Error -> {
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

        is SummarizeAnswerUiState.Idle -> Unit
    }
}