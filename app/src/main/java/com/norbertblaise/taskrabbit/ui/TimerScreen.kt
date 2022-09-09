package com.norbertblaise.taskrabbit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.ui.components.CircularProgressBar
import com.norbertblaise.taskrabbit.ui.theme.Grey
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500

@Composable
fun TimerScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier,
                backgroundColor = Color.White,
                contentColor = Ink,
                elevation = 0.dp,
                title = { Text(text = "") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Settings, contentDescription = null)/*todo add CD*/
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chart_outlined),
                            contentDescription = null
                        )
                    }
                }

            )
        },

        ) {
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
                    onClick = { /*TODO*/ },
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
}


@Preview
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}