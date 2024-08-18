package com.leodemo.genai_android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leodemo.genai_android.ui.component.shape.ChatBubbleShape

@Composable
fun UserMessageBubble(
    text: String,
    margin: Dp = 5.dp
) {
    if (text.isBlank()) return
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val fraction = 0.8f
        val maxAllowedWidth = maxWidth * fraction
        Box(
            modifier = Modifier
                .widthIn(max = maxAllowedWidth)
                .align(Alignment.CenterEnd)
                .padding(top = 10.dp, end = 10.dp)
                .clip(ChatBubbleShape(margin = margin))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(bottom = margin, end = margin)
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}