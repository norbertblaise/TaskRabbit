package com.norbertblaise.taskrabbit.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.ui.theme.Ink

@Composable
fun SettingsTopAppBar(title: String) {
    TopAppBar(
        title = {
            Text(title, color = Ink)
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