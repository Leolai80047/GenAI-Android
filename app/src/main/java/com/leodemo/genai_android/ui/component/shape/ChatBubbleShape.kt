package com.leodemo.genai_android.ui.component.shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class ChatBubbleShape(private val margin: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val marginPx = with(density) { margin.toPx() }
        val cornerRadius = with(density) { 10.dp.toPx() }
        val path = Path().apply {
            moveTo(size.width, size.height)
            lineTo(size.width - marginPx - cornerRadius, size.height - marginPx)
            lineTo(cornerRadius, size.height - marginPx)
            quadraticTo(0f, size.height - marginPx, 0f, size.height - marginPx - cornerRadius)
            lineTo(0f, cornerRadius)
            quadraticTo(0f, 0f, cornerRadius, 0f)
            lineTo(size.width - marginPx - cornerRadius, 0f)
            quadraticTo(size.width - marginPx, 0f, size.width - marginPx, cornerRadius)
            lineTo(size.width - marginPx, size.height - marginPx - cornerRadius)
            lineTo(size.width - marginPx, size.height - marginPx - cornerRadius)
            close()
        }

        return Outline.Generic(path)
    }

}