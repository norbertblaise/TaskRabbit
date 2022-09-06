package com.norbertblaise.taskrabbit.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressBar(
    percentage: Float,
    radius: Dp = 50.dp,
    color: Color,

    ) {
    val currPercentage = animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000, delayMillis = 0)
    )
    Box(
        modifier = Modifier.size(
            radius * 2f
        )
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currPercentage.value,
                useCenter = false,
                style = Stroke(36.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        )
        Text(text = currPercentage.value.toString())
    }
}