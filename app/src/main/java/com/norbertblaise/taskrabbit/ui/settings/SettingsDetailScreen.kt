package com.norbertblaise.taskrabbit.ui.settings

import android.widget.Space
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.ui.components.SettingsTopAppBar
import com.norbertblaise.taskrabbit.ui.theme.Charcoal
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500

val focusTimeOptions = listOf(10, 25, 55, 90, "Custom")
val shortBreakOptions = listOf(5, 10, 15, 20, "Custom")
val longBreakOptions = listOf(20, 30, 40, 50, "Custom")
val longBreakIntervalOptions = listOf(4, 6, 8, "Custom")
val radioOptions = focusTimeOptions //todo make this dymamic depending on the settings type

//todo make recieve screen title along with value to modify
@Composable
fun SettingsDetailScreen() {
    Scaffold(topBar = { SettingsTopAppBar(title = "Settings Detail") }) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "how long do you want to focus for?",
                style = MaterialTheme.typography.body1,
                color = Charcoal
            )
            Spacer(Modifier.height(14.dp))
            SettingsOptions()
        }
    }
}

@Composable
fun SettingsOptions() {
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) } //todo get default value from stored value
    val unit =
        "min" //todo if settingsOption is not longBreakInterval then unit is "min" else "Pomos"
    Column(modifier = Modifier.selectableGroup()) {
        radioOptions.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .selectable(
                        selected = (it == selectedOption),
                        onClick = { onOptionSelected(it) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
//                RadioButton(selected = (it == selectedOption),
//                    colors = RadioButtonColors.radioColor(enabled = Ink, selected = Salmon500), onClick = null)
                Spacer(Modifier.padding(horizontal = 11.dp))
                //todo if selected option is the last, then enable textField
                Text(
                    text = if (it == radioOptions.lastIndex) "$it"
                    else "$it $unit"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsDetailScreenPreview() {
    SettingsDetailScreen()
}