package com.leodemo.genai_android.ui.screens.PhotoDescribeScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.GenAITopAppBar

@Composable
fun PhotoDescribeScreen() {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            GenAITopAppBar(title = stringResource(R.string.app_name))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            PhotoDescribeAnswerArea()
            PhotoInputField()
        }
    }
}

@Composable
private fun ColumnScope.PhotoDescribeAnswerArea() {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null
            )
            HorizontalDivider(Modifier.height(1.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Answer",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PhotoInputField() {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    Row(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondary)
    ) {
        BasicTextField(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp),
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
            },
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
                    PhotoDescribeInputFieldTrailIcon(painter = painterResource(R.drawable.ic_mic))
                    PhotoDescribeInputFieldTrailIcon(painter = painterResource(R.drawable.ic_camera))
                }
            }
        )
        IconButton(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically),
            onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.Send,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }

}

@Composable
private fun PhotoDescribeInputFieldTrailIcon(
    painter: Painter
) {
    IconButton(
        modifier = Modifier.size(30.dp),
        onClick = { /*TODO*/ }) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}