package com.norbertblaise.taskrabbit.ui.pomodoro

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.ui.components.CircularProgressBar
import com.norbertblaise.taskrabbit.ui.theme.Grey
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import androidx.lifecycle.viewmodel.compose.viewModel

private const val TAG = "PomodoroScreen"

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = viewModel(),
    onSettingsClick: () -> Unit = {},
    onChartClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            PomodoroScreenAppBar(
                onSettingsClicked = onSettingsClick,
                onChartClicked = onChartClick
            )
        },
        content = {
            PomodoroScreenBody(viewModel = viewModel)
        }


    )


}

@Composable
fun PomodoroScreenAppBar(
    onSettingsClicked: () -> Unit = {},
    onChartClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text("") },
        backgroundColor = Color.White,
        elevation = 0.dp,
        actions = {
            IconButton(onClick =  onChartClicked ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chart_outlined),
                    tint = Ink,
                    contentDescription = "View Stats button" // todo extract string resource
                )
            }
            IconButton(onClick =  onSettingsClicked ) {
                Icon(
                    Icons.Default.Settings,
                    tint = Ink,
                    contentDescription = "Open Settings" //todo convert to string resours
                )
            }
        }
    )
}

@Composable
fun PomodoroScreenBody(viewModel: PomodoroViewModel) {
    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Task Rabbit",
            style = MaterialTheme.typography.h2
        )
        Spacer(modifier = Modifier.height(80.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularProgressBar(
                percentage = 0.7f,
                timerValue = viewModel.currentTimeLeft,
                label = "Focus",
                currentPom = "1",
                numPoms = "4",
                color = Salmon500
            )
        }
        Spacer(modifier = Modifier.height(70.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(40.dp),
                backgroundColor = Grey,
                contentColor = Ink
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_replay),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            ExtendedFloatingActionButton(
                onClick = {
                    Log.d(TAG, "PomodoroScreenBody: startbutton clicked")/*TODO*/
                    viewModel.startStopButtonClicked()
                },
                icon = {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                },
                text = { Text(text = "Start") },
                modifier = Modifier,
                backgroundColor = Salmon500,
                contentColor = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(40.dp),
                backgroundColor = Grey,
                contentColor = Ink
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_skip_next),
                    contentDescription = null
                )
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PomodoroScreenTopAppBarPreview() {
    PomodoroScreenAppBar()
}

@Preview(showBackground = true)
@Composable
fun PomodoroScreenBodyPreview() {
    PomodoroScreenBody(viewModel = viewModel())
}

@Preview(showBackground = true)
@Composable
fun PomodoroScreenPreview() {
    PomodoroScreen()
}