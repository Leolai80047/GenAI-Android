package com.leodemo.genai_android.ui.screens.photoDescribe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.ChatTextField
import com.leodemo.genai_android.ui.component.DefaultChatTextFieldActionButton
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.ui.component.StyledAnswerText
import com.leodemo.genai_android.ui.component.UserMessageBubble
import com.leodemo.genai_android.utils.extensions.toBitmap

@Composable
fun PhotoDescribeScreen(
    viewModel: PhotoDescribeViewModel = hiltViewModel(),
    startCamera: () -> Unit,
) {
    val context = LocalContext.current
    val answerUiState by viewModel.answerUiState.collectAsStateWithLifecycle()
    val promptUiState by viewModel.promptUiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            GenAITopAppBar(title = stringResource(R.string.app_name))
        }
    ) { paddingValues ->
        PhotoDescribeContent(
            modifier = Modifier.padding(paddingValues),
            promptUiState = promptUiState,
            answerUiState = answerUiState,
            onSelectImage = viewModel::setSelectedUri,
            onSend = { prompt ->
                promptUiState.uri.toBitmap(context)?.let { bitmap ->
                    viewModel.send(bitmap, prompt)
                }
            },
            startCamera = startCamera
        )
    }
}

@Composable
private fun PhotoDescribeContent(
    modifier: Modifier,
    promptUiState: PhotoDescribePromptUiState,
    answerUiState: PhotoDescribeAnswerUiState,
    onSelectImage: (Uri) -> Unit,
    onSend: (String) -> Unit,
    startCamera: () -> Unit
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        PhotoDescribeAnswerStateView(answerUiState = answerUiState)
        PhotoDescribeChatArea(
            promptUiState = promptUiState,
            answer = answerUiState.data,
        )
        PhotoDescribeInputField(
            answerUiState = answerUiState,
            onSelectImage = onSelectImage,
            onSend = onSend,
            startCamera = startCamera
        )
    }
}

@Composable
private fun ColumnScope.PhotoDescribeChatArea(
    promptUiState: PhotoDescribePromptUiState,
    answer: String,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        UserMessageBubble(text = promptUiState.question)
        PhotoDescribeAnswerArea(
            uri = promptUiState.uri ?: Uri.EMPTY,
            answer = answer
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PhotoDescribeAnswerArea(
    uri: Uri,
    answer: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        if (uri == Uri.EMPTY) return@Card
        GlideImage(
            modifier = Modifier
                .size(100.dp)
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            model = uri,
            contentDescription = "",
            contentScale = ContentScale.Fit,
            alignment = Alignment.Center
        ) {
            it.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        }
        if (answer.isBlank()) return@Card
        HorizontalDivider(
            modifier = Modifier
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
        )
        StyledAnswerText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            text = answer,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun PhotoDescribeInputField(
    answerUiState: PhotoDescribeAnswerUiState,
    onSelectImage: (Uri) -> Unit,
    onSend: (String) -> Unit,
    startCamera: () -> Unit,
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    ChatTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
        },
        trailingIcons = {
            PhotoDescribeImagePicker(onSetImage = onSelectImage)
            PhotoDescribeCameraCapture(startCamera = startCamera)
        },
        actionButton = {
            DefaultChatTextFieldActionButton(
                enable = answerUiState !is PhotoDescribeAnswerUiState.Loading,
                onClick = {
                    onSend(textFieldValue.text)
                    textFieldValue = TextFieldValue("")
                }
            )
        }
    )
}

@Composable
private fun PhotoDescribeInputFieldTrailIcon(
    painter: Painter,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(30.dp),
        onClick = onClick
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun PhotoDescribeImagePicker(
    onSetImage: (Uri) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            onSetImage(it)
        }
    }

    PhotoDescribeInputFieldTrailIcon(
        painter = painterResource(R.drawable.ic_image),
        onClick = {
            launcher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    )
}

@Composable
private fun PhotoDescribeCameraCapture(
    startCamera: () -> Unit
) {
    PhotoDescribeInputFieldTrailIcon(painter = painterResource(R.drawable.ic_camera)) {
        startCamera()
    }
}

@Composable
private fun PhotoDescribeAnswerStateView(
    answerUiState: PhotoDescribeAnswerUiState
) {
    when (answerUiState) {
        is PhotoDescribeAnswerUiState.Loading -> {
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

        is PhotoDescribeAnswerUiState.Error -> {
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

        is PhotoDescribeAnswerUiState.Idle -> Unit
    }
}