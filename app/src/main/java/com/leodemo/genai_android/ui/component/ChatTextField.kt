package com.leodemo.genai_android.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leodemo.genai_android.utils.TestTags

@Composable
fun ChatTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    trailingIcons: @Composable RowScope.() -> Unit = {},
    actionButton: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
    ) {
        BasicTextField(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
                .testTag(TestTags.CHAT_TEXT_FIELD),
            value = value,
            onValueChange = onValueChange,
            maxLines = 1,
            textStyle = TextStyle(
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(25.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        innerTextField()
                    }
                    SpeechRecognizerButton(
                        onResult = { result ->
                            onValueChange(TextFieldValue(result))
                        }
                    )
                    trailingIcons()
                }
            }
        )
        actionButton()
    }
}

@Composable
fun RowScope.DefaultChatTextFieldActionButton(
    enable: Boolean = true,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(50.dp)
            .align(Alignment.CenterVertically)
            .testTag(TestTags.CHAT_TEXT_SEND),
        enabled = enable,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Send,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}