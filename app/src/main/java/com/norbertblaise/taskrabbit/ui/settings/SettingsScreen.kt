package com.norbertblaise.taskrabbit.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.norbertblaise.taskrabbit.ui.components.SettingsTopAppBar
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
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
        content = { innerPadding ->
            SettingsScreenBody(
                viewModel = viewModel(),
                modifier = Modifier.padding(innerPadding),
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
    viewModel: SettingsViewModel,
    onSettingsItemClicked: (Int) -> Unit = {},
    onShortBreakClicked: (Int) -> Unit = {},
    onLongBreakClicked: (Int) -> Unit = {},
    onLongBreakIntervalClicked: (Int) -> Unit = {},
    modifier: Modifier
) {
    val focusTime = viewModel.focusTimeFlow.collectAsState()
    val shortBreakTime = viewModel.shortBreakFlow.collectAsState()
    val longBreakTime = viewModel.longBreakFlow.collectAsState()
    val longBreakInterval = viewModel.longBreakIntervalFlow.collectAsState()
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
            value = "${focusTime.value} min",
            onClick = { onSettingsItemClicked(0) })
        SettingsListItem(
            label = "Short Break",
            value = "${shortBreakTime.value} min",
            onClick = { onSettingsItemClicked(1) })
        SettingsListItem(
            label = "Long Break",
            value = "${longBreakTime.value} min",
            onClick = { onSettingsItemClicked(2) })
        SettingsListItem(
            label = "Long Break Interval",
            value = "${longBreakInterval.value} Pomos",
            onClick = { onSettingsItemClicked(3) })

    }
}

@Composable
fun SettingsListItem(
    label: String,
    value: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
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
    SettingsScreenBody(viewModel = viewModel(), modifier = Modifier.padding())
}