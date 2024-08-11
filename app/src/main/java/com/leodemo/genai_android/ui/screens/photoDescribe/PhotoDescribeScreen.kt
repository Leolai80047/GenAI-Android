package com.leodemo.genai_android.ui.screens.photoDescribe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.leodemo.genai_android.R
import com.leodemo.genai_android.ui.component.GenAITopAppBar
import com.leodemo.genai_android.ui.component.SpeechRecognizerButton
import com.leodemo.genai_android.ui.component.StyledAnswerText

@Composable
fun PhotoDescribeScreen(
    viewModel: PhotoDescribeViewModel = hiltViewModel(),
    startCamera: () -> Unit,
    clearBitmap: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            GenAITopAppBar(title = stringResource(R.string.app_name))
        }
    ) { paddingValues ->
        val answer by viewModel.answer.observeAsState("")
        val captureBitmap by viewModel.bitmap.collectAsStateWithLifecycle()

        PhotoDescribeContent(
            modifier = Modifier.padding(paddingValues),
            answer = answer,
            captureBitmap = captureBitmap,
            clearBitmap = {
                clearBitmap()
                viewModel.setSelectedBitmap(null)
            },
            onSend = { bitmap, prompt ->
                viewModel.send(bitmap, prompt)
            },
            startCamera = startCamera
        )
    }
}

@Composable
private fun PhotoDescribeContent(
    modifier: Modifier,
    answer: String,
    captureBitmap: Bitmap?,
    clearBitmap: () -> Unit,
    onSend: (Bitmap, String) -> Unit,
    startCamera: () -> Unit
) {
    fun calculateSampleSize(options: Options, reqWidth: Int, reqHeight: Int): Int {
        var sampleSize = 1
        val (width: Int, height: Int) = options.run {
            outWidth to outHeight
        }

        if (width > reqWidth || height > reqHeight) {
            val halfWidth = width / 2
            val halfHeight = height / 2

            while ((halfWidth / sampleSize) >= reqWidth || (halfHeight / sampleSize) >= reqHeight) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        val contentResolver = context.contentResolver
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            var bitmap: Bitmap? = null
            inputStream?.use {
                val options = Options().apply {
                    inJustDecodeBounds = true
                }
                options.apply {
                    val size = 768
                    inSampleSize = calculateSampleSize(options, size, size)
                    inJustDecodeBounds = false
                }

                bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            }
            bitmap
        } catch (e: Exception) {
            Toast.makeText(context, "Bitmap convert error!", Toast.LENGTH_LONG).show()
            null
        }
    }

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ) {
        val context = LocalContext.current
        var imageUri by remember {
            mutableStateOf<Uri>(Uri.EMPTY)
        }
        PhotoDescribeAnswerArea(
            answer = answer,
            selectedImageUri = imageUri,
            captureBitmap = captureBitmap
        )
        PhotoInputField(
            onSetImage = {
                clearBitmap()
                imageUri = it
            },
            onSend = onSend@{ prompt ->
                val bitmap = if (captureBitmap == null) {
                    uriToBitmap(context, imageUri) ?: return@onSend
                } else {
                    captureBitmap
                }
                onSend(bitmap, prompt)
            },
            startCamera = startCamera
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ColumnScope.PhotoDescribeAnswerArea(
    answer: String,
    selectedImageUri: Uri,
    captureBitmap: Bitmap?
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
            if (selectedImageUri == Uri.EMPTY && captureBitmap == null) return@Card
            GlideImage(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                model = captureBitmap ?: selectedImageUri,
                contentDescription = "",
                contentScale = ContentScale.Fit,
                alignment = Alignment.Center
            )
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
    onSetImage: (Uri) -> Unit,
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
                    PhotoDescribeImagePicker(onSetImage = onSetImage)
                    PhotoDescribeCameraCapture(startCamera = startCamera)
                }
            }
        )
        IconButton(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterVertically),
            onClick = {
                onSend(textFieldValue.text)
                textFieldValue = TextFieldValue("")
            }
        ) {
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