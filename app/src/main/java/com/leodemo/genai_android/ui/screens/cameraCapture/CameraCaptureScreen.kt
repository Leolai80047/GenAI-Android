package com.leodemo.genai_android.ui.screens.cameraCapture

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leodemo.genai_android.R
import com.leodemo.genai_android.utils.extensions.saveToUri
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    viewModel: CameraCaptureViewModel = hiltViewModel(),
    onBack: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )
    val bitmaps by viewModel.bitmaps.collectAsStateWithLifecycle()
    var isLoading by remember {
        mutableStateOf(false)
    }
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        sheetContent = {
            CameraCaptureBottomSheet(
                bitmaps = bitmaps,
                onBitmapSelect = { bitmap ->
                    isLoading = true
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.hide()
                        val uri = bitmap.saveToUri(context, "image.png")
                        onBack(uri)
                    }
                },
            )
        }
    ) {
        CameraCapturePermissionContent(
            onBack = onBack,
            content = {
                CameraCaptureContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    openBottomSheet = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    onPictureTaken = viewModel::takenPicture,
                    switchCamera = viewModel::switchCamera
                )
            }
        )
        if (isLoading) {
            CameraCaptureFullScreenOverlayLoading()
        }
    }
}

@Composable
private fun CameraCapturePermissionContent(
    onBack: (Uri?) -> Unit,
    content: @Composable () -> Unit
) {
    var hasPermission by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                hasPermission = true
            } else {
                Toast.makeText(context, "No camera permission!", Toast.LENGTH_LONG).show()
                onBack(null)
            }
        }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasPermission = true
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        content()
    }
}

@Composable
private fun CameraCaptureContent(
    modifier: Modifier = Modifier,
    openBottomSheet: () -> Unit,
    onPictureTaken: (ImageProxy) -> Unit,
    switchCamera: (LifecycleCameraController) -> Unit
) {
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }
    Box(
        modifier = modifier
    ) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            controller = cameraController
        )
        CameraSwitcher(
            modifier = Modifier
                .statusBarsPadding()
                .offset(10.dp, 10.dp),
            onClick = {
                switchCamera(cameraController)
            }
        )
        CameraCaptureBottomTool(
            cameraController = cameraController,
            openBottomSheet = openBottomSheet,
            onPictureTaken = onPictureTaken
        )

    }
}

@Composable
private fun CameraCaptureBottomSheet(
    bitmaps: List<Bitmap>,
    onBitmapSelect: (Bitmap) -> Unit
) {
    if (bitmaps.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "No photo found!",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
    LazyVerticalStaggeredGrid(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        columns = StaggeredGridCells.Fixed(3),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp
    ) {
        items(bitmaps) { bitmap ->
            Image(
                modifier = Modifier.clickable {
                    onBitmapSelect(bitmap)
                },
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    controller: LifecycleCameraController
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = modifier,
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
        }
    )
}

@Composable
private fun CameraSwitcher(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painterResource(R.drawable.ic_camera_switch),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null
        )
    }
}

@Composable
private fun BoxScope.CameraCaptureBottomTool(
    cameraController: LifecycleCameraController,
    openBottomSheet: () -> Unit,
    onPictureTaken: (ImageProxy) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(30.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        CameraCaptureImageSelector(openBottomSheet = openBottomSheet)
        CameraCapturePictureTaker(
            cameraController = cameraController,
            onPictureTaken = onPictureTaken
        )
    }
}

@Composable
private fun CameraCaptureImageSelector(
    openBottomSheet: () -> Unit
) {
    CameraCaptureIcon(
        painter = painterResource(R.drawable.ic_image),
        onClick = openBottomSheet
    )
}

@Composable
private fun CameraCapturePictureTaker(
    cameraController: LifecycleCameraController,
    onPictureTaken: (ImageProxy) -> Unit
) {
    val context = LocalContext.current
    val imageCapturedCallback = object : OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            onPictureTaken(image)
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            super.onError(exception)
            Toast.makeText(context, "Camera capture error!", Toast.LENGTH_LONG)
                .show()
        }
    }
    CameraCaptureIcon(
        painter = painterResource(R.drawable.ic_camera),
        onClick = {
            cameraController.takePicture(
                ContextCompat.getMainExecutor(context),
                imageCapturedCallback
            )
        }
    )
}

@Composable
private fun CameraCaptureIcon(
    painter: Painter,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(50.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            ),
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier.size(30.dp),
            painter = painter,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null
        )
    }
}

@Composable
private fun CameraCaptureFullScreenOverlayLoading() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.5f))
        .clickable {}
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary
        )
    }
}