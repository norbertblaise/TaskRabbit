package com.norbertblaise.taskrabbit.ui.settings

import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500

@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            SettingsTopAppBar()
        },
        content = {

        }
    )
}

@Composable
fun SettingsTopAppBar() {
    TopAppBar(
        title = {
            Text("Settings", color = Ink)
        },
        backgroundColor = Color.White,
        contentColor = Ink,
        elevation = 0.dp,
        navigationIcon = {
            IconButton(
                onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Default.ArrowBack,
                    tint = Ink,
                    contentDescription = "back button"//todo extract string reseouce
                )
            }
        }
    )
}

@Composable
fun SettingsScreenBody() {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Timer Settings",
            style = MaterialTheme.typography.h4,
            color = Ink
        )
        Spacer(modifier = Modifier.height(12.dp))
        //todo add list of settings items
    }
}

@Composable
fun SettingsListItem(
    //todo make clickable
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                "Focus Time",//todo replace with variable
                style = MaterialTheme.typography.h5,
                color = Ink
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "25 min", //todo replace with variable
                style = MaterialTheme.typography.body1,
                color = Salmon500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsTopAppBarPreview() {
    SettingsTopAppBar()
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenBodyPreview() {
    SettingsScreenBody()
}