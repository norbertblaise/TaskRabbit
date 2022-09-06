package com.norbertblaise.taskrabbit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TimerScreen(){
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.background(color = Color.White),
                title = {Text(text = "")},
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Settings, contentDescription = null )/*todo add CD*/
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Outlined.Info, contentDescription = null )
                    }
                }

            )
        },

    ) {
        Column {
            Text(text = "Task Rabbit")
            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}


@Preview
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}