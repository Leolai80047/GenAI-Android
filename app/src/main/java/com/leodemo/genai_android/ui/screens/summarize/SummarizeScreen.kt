package com.leodemo.genai_android.ui.screens.summarize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.ChatTextField
import com.leodemo.genai_android.ui.component.DefaultChatTextFieldActionButton
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.ui.component.StyledAnswerText


@Composable
fun SummarizeScreen(
    viewModel: SummarizeViewModel = hiltViewModel()
) {
    val answer by viewModel.answer.observeAsState("")

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
                SummarizeInputField(
                    onSend = { inputText ->
                        viewModel.answer.value = ""
                        viewModel.sendMessage(inputText)
                    }
                )
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
    StyledAnswerText(
        modifier = Modifier.padding(10.dp),
        text = description,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun SummarizeInputField(
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
                },
            )
        }
    )
}