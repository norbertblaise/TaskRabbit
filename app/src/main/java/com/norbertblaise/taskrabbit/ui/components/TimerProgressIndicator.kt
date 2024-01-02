package com.norbertblaise.taskrabbit.ui.components

import android.text.format.DateUtils
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.norbertblaise.taskrabbit.ui.theme.Grey
import com.norbertblaise.taskrabbit.ui.theme.Salmon500


fun formatTime(timeInLong: Long): String {
    return DateUtils.formatElapsedTime(timeInLong/1000)

}

@Composable
fun TimerProgressIndicator(
    percentage: Float,
    initialTimerValue: Long =0L,
    timeLeft: Long,
    label: String,
    currentPom: String,
    numPoms: String,
//    isTimerRunning: Boolean?,
    radius: Dp = 320.dp,
    color: Color,


    ) {
    var value by remember {
        mutableStateOf(percentage)
    }
//    Timber.d("TimerProgressIndicator: value is $value")
    var currPercentage = animateFloatAsState(
        targetValue = value,
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
                    sweepAngle = -360f * percentage,
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
                text = formatTime(timeLeft),
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
    TimerProgressIndicator(
        percentage = 0.5f,
        initialTimerValue = 1000L,
        timeLeft = 1000L,
        label = "Focus",
        currentPom = "2",
        numPoms = "4",
//        isTimerRunning = false,
        color = Salmon500
    )
}