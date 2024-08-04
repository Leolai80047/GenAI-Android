package com.leodemo.chat_room

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.leodemo.chat_room.ui.theme.ChatRoomTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            ChatRoomTheme {
                ChatRoom(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatRoom(viewModel: MainViewModel) {
    val answer by viewModel.answer.observeAsState("")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Chat Room")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray
                )
            )
        },
        content = { paddingValues ->
            Column(
                Modifier
                    .navigationBarsPadding()
                    .padding(paddingValues)
            ) {
                ChatRoomAnswerArea(answer = answer)
                ChatRoomInputField(
                    sendMessage = { questionMessage ->
                        viewModel.sendMessage(questionMessage)
                        viewModel.answer.value = ""
                    }
                )
            }
        }
    )
}

@Composable
private fun ColumnScope.ChatRoomAnswerArea(answer: String) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .weight(1f)
            .padding(10.dp)
    ) {
        ChatRoomText(answer)
    }
}

@Composable
private fun ChatRoomText(description: String) {
    // Remembers the HTML formatted description. Re-executes on a new description
    val htmlDescription = remember(description) {
        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
                setTextColor(ContextCompat.getColor(context, R.color.black))
            }
        },
        update = {
            it.text = htmlDescription
        }
    )
}

@Composable
private fun ChatRoomInputField(
    sendMessage: (String) -> Unit
) {
    var inputText by remember {
        mutableStateOf("")
    }
    Row {
        TextField(
            modifier = Modifier
                .wrapContentHeight()
                .weight(1f),
            value = inputText,
            onValueChange = {
                inputText = it
            })
        IconButton(
            modifier = Modifier.size(50.dp),
            onClick = onClick@{
                if (inputText.isBlank()) return@onClick
                sendMessage(inputText)
                inputText = ""
            }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null
            )
        }
    }
}