package com.leodemo.genai_android.ui.screens.cameraCapture

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CameraCaptureViewModel @Inject constructor() : ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    private var cameraSelectorState = CameraSelector.DEFAULT_BACK_CAMERA

    fun switchCamera(cameraController: LifecycleCameraController) {
        cameraSelectorState =
            if (cameraController.cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
        cameraController.cameraSelector = cameraSelectorState
    }

    fun takenPicture(image: ImageProxy) {
        _bitmaps.value += processImage(image)
    }

    private fun processImage(image: ImageProxy): Bitmap {
        val matrix = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
            if (cameraSelectorState == CameraSelector.DEFAULT_FRONT_CAMERA) {
                postScale(-1f, 1f)
            }
        }
        return Bitmap.createBitmap(
            image.toBitmap(),
            0,
            0,
            image.width,
            image.height,
            matrix,
            true
        )
    }
}