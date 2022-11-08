package com.norbertblaise.taskrabbit.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.ui.components.SettingsTopAppBar
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500

@Composable
fun SettingsScreen(
    onSettingsItemClicked: (Int) -> Unit = {},
    onShortBreakClicked: (Int) -> Unit = {},
    onLongBreakClicked: (Int) -> Unit = {},
    onLongBreakIntervalClicked: (Int) -> Unit = {},
    onUpButtonClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                title = "Settings",
                onUpButtonClicked = onUpButtonClicked
            )
        },
        content = {
            SettingsScreenBody(
                onSettingsItemClicked = onSettingsItemClicked,
//                onShortBreakClicked = onShortBreakClicked,
//                onLongBreakClicked = onLongBreakClicked,
//                onLongBreakIntervalClicked = onLongBreakIntervalClicked
            )
        }
    )
}


@Composable
fun SettingsScreenBody(
    onSettingsItemClicked: (Int) -> Unit = {},
    onShortBreakClicked: (Int) -> Unit = {},
    onLongBreakClicked: (Int) -> Unit = {},
    onLongBreakIntervalClicked: (Int) -> Unit = {},
) {
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
        SettingsListItem(
            label = "Focus Time",
            value = "25 min",
            onClick = { onSettingsItemClicked(0) })
        SettingsListItem(
            label = "Short Break",
            value = "5 min",
            onClick = { onSettingsItemClicked(1) })
        SettingsListItem(
            label = "Long Break",
            value = "20 min",
            onClick = { onSettingsItemClicked(2) })
        SettingsListItem(
            label = "Long Break Interval",
            value = "4 Pomos",
            onClick = { onSettingsItemClicked(3) })

    }
}

@Composable
fun SettingsListItem(
    label: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Row(modifier = Modifier
        .padding(bottom = 8.dp)
        .fillMaxWidth()
        .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                "$label",
                style = MaterialTheme.typography.h5,
                color = Ink
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "$value",
                style = MaterialTheme.typography.body1,
                color = Salmon500
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsTopAppBarPreview() {
    SettingsTopAppBar("Settings",
        onUpButtonClicked = {/*do nothing*/ })
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenBodyPreview() {
    SettingsScreenBody()
}