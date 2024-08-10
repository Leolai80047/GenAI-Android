package com.leodemo.genai_android.ui.screens.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.ui.screens.Screen

@Composable
fun MenuScreen(
    navigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            GenAITopAppBar(stringResource(R.string.app_name))
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            MenuItem(text = "Summarize") {
                navigate(Screen.SummarizeScreen)
            }
            MenuItem(text = "Text_Image") {
                navigate(Screen.PhotoDescribeScreen)
            }
            MenuItem(text = "Chat Room") {
                navigate(Screen.ChatScreen)
            }
        }
    }
}

@Composable
private fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(30.dp)
            .clickable {
                onClick()
            }
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = text,
            textAlign = TextAlign.Center
        )
    }
}