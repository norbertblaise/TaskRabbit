package com.norbertblaise.taskrabbit.ui.settings

import android.widget.Space
import androidx.compose.foundation.clickable
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
import com.norbertblaise.taskrabbit.ui.components.SettingsTopAppBar
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500

@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            SettingsTopAppBar(title = "Settings")
        },
        content = {

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
        SettingsListItem(label = "Focus Time", value = "25 min")
        SettingsListItem(label = "Short Break", value = "5 min")
        SettingsListItem(label = "Long Break", value = "20 min")
        SettingsListItem(label = "Long Break Interval", value = "4 Pomos")
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
        .clickable { onClick}
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
    SettingsTopAppBar("Settings")
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenBodyPreview() {
    SettingsScreenBody()
}