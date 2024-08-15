package com.leodemo.genai_android.ui.screens.photoDescribe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.ui.component.SpeechRecognizerButton
import com.leodemo.genai_android.ui.component.StyledAnswerText
import com.leodemo.genai_android.utils.extensions.toBitmap

@Composable
fun PhotoDescribeScreen(
    viewModel: PhotoDescribeViewModel = hiltViewModel(),
    startCamera: () -> Unit,
) {
    val context = LocalContext.current
    val answerState by viewModel.answerState.collectAsStateWithLifecycle()
    val imageUri by viewModel.imageUri.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            GenAITopAppBar(title = stringResource(R.string.app_name))
        }
    ) { paddingValues ->
        PhotoDescribeContent(
            modifier = Modifier.padding(paddingValues),
            answerState = answerState,
            imageUri = imageUri ?: Uri.EMPTY,
            onSelectImage = viewModel::setSelectedUri,
            onSend = { prompt ->
                imageUri.toBitmap(context)?.let { bitmap ->
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
    answerState: PhotoDescribeUiState,
    imageUri: Uri,
    onSelectImage: (Uri) -> Unit,
    onSend: (String) -> Unit,
    startCamera: () -> Unit
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        PhotoDescribeAnswerStateView(answerState = answerState)
        PhotoDescribeAnswerArea(
            answer = answerState.data,
            selectedImageUri = imageUri
        )
        PhotoInputField(
            onSelectImage = onSelectImage,
            onSend = onSend,
            startCamera = startCamera
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColumnScope.PhotoDescribeAnswerArea(
    answer: String,
    selectedImageUri: Uri,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            if (selectedImageUri == Uri.EMPTY) return@Card
            GlideImage(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally),
                model = selectedImageUri,
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
                    .background(MaterialTheme.colorScheme.onPrimaryContainer)
            )
            StyledAnswerText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = answer,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun PhotoInputField(
    onSelectImage: (Uri) -> Unit,
    onSend: (String) -> Unit,
    startCamera: () -> Unit,
) {
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
                    SpeechRecognizerButton(
                        content = {
                            PhotoDescribeInputFieldTrailIcon(
                                painter = painterResource(R.drawable.ic_mic),
                                onClick = it
                            )
                        },
                        onResult = {
                            textFieldValue = TextFieldValue(it)
                        }
                    )
                    PhotoDescribeImagePicker(onSetImage = onSelectImage)
                    PhotoDescribeCameraCapture(startCamera = startCamera)
                }
            }
        )
        PhotoDescribeSendButton(
            onClick = {
                onSend(textFieldValue.text)
                textFieldValue = TextFieldValue("")
            }
        )
    }

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
private fun RowScope.PhotoDescribeSendButton(
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(50.dp)
            .align(Alignment.CenterVertically),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.Send,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary
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
    answerState: PhotoDescribeUiState
) {
    when (answerState) {
        is PhotoDescribeUiState.Loading -> {
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

        is PhotoDescribeUiState.Error -> {
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

        is PhotoDescribeUiState.Idle -> Unit
    }
}