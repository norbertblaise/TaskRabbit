package com.norbertblaise.taskrabbit.ui.components

import android.os.Build
import android.text.format.DateUtils
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertblaise.taskrabbit.ui.theme.Grey
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import java.time.format.DateTimeFormatter


fun formatTime(timeInLong: Long): String {
    return DateUtils.formatElapsedTime(timeInLong)
}

@Composable
fun CircularProgressBar(
    percentage: Float,
    timerValue: Long,
    label: String,
    currentPom: String,
    numPoms: String,
    radius: Dp = 320.dp,
    color: Color,


    ) {
    val currPercentage = animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 1000, delayMillis = 0)
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(
            radius
        )
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(radius),
            onDraw = {

                drawArc(

                    color = Grey,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )
                drawArc(

                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360 * currPercentage.value,
                    useCenter = false,
                    style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height)
                )

            }
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.h3,

                )
            Text(
                text = formatTime(timerValue),
                style = MaterialTheme.typography.h1
            )
            Text(
                "$currentPom of $numPoms",
                style = TextStyle(
                    fontSize = 24.sp
                )
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CircularProgressBarPreview() {
    CircularProgressBar(
        percentage = 0.5f,
        timerValue = 1000L,
        label = "Focus",
        currentPom = "2",
        numPoms = "4",
        color = Salmon500
    )
}