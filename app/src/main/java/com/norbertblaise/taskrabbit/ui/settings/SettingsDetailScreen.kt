package com.norbertblaise.taskrabbit.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.norbertblaise.taskrabbit.common.descriptionsList
import com.norbertblaise.taskrabbit.common.settingParameter
import com.norbertblaise.taskrabbit.models.TimerSettingsParameter
import com.norbertblaise.taskrabbit.ui.components.SettingsTopAppBar
import com.norbertblaise.taskrabbit.ui.theme.Charcoal
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon100
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import java.util.Timer

val focusTimeOptions = listOf(10, 25, 55, 90, "Custom")
val shortBreakOptions = listOf(5, 10, 15, 20, "Custom")
val longBreakOptions = listOf(20, 30, 40, 50, "Custom")
val longBreakIntervalOptions = listOf(4, 6, 8, "Custom")

//todo make recieve screen title along with value to modify

fun mapIntToTimerSettingsParameter(value: Int): TimerSettingsParameter {
    return when (value) {
        0 -> TimerSettingsParameter.FOCUS_TIME
        1 -> TimerSettingsParameter.SHORT_BREAK
        2 -> TimerSettingsParameter.LONG_BREAK
        3 -> TimerSettingsParameter.LONG_BREAK_INTERVAL
        else -> throw error("only int 0 - 3 are valid params")
    }
}

@Composable
fun SettingsDetailScreen(arg: Int) {
    val timerSettingsParameter = mapIntToTimerSettingsParameter(arg)
    //map int to
    val radioOptions = when (timerSettingsParameter) {
        TimerSettingsParameter.FOCUS_TIME -> focusTimeOptions
        TimerSettingsParameter.SHORT_BREAK -> shortBreakOptions
        TimerSettingsParameter.LONG_BREAK -> longBreakOptions
        TimerSettingsParameter.LONG_BREAK_INTERVAL -> longBreakIntervalOptions
    }

    var textValue by remember {
        mutableStateOf(TextFieldValue(""))
    }
    val unit = if (timerSettingsParameter == TimerSettingsParameter.LONG_BREAK_INTERVAL) "Pomos"
    else "min"


    Scaffold(topBar = { SettingsTopAppBar(title = settingParameter[timerSettingsParameter.type]) }) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = descriptionsList[timerSettingsParameter.type],
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Charcoal
            )
            Spacer(Modifier.height(14.dp))
            SettingsOptions(unit = unit, radioOptions = radioOptions)
            Spacer(Modifier.height(14.dp))
            TextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                },
                label = { Text(unit) },
                placeholder = { Text("25") },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Ink,
                    cursorColor = Salmon500,
                    backgroundColor = Salmon100,
                    focusedLabelColor = Salmon500,
                    unfocusedLabelColor = Ink
                ),
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}

@Composable
fun SettingsOptions(unit: String, radioOptions: List<Any>) {
    //todo hoist this state to viewModel
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) } //todo get default value from stored value

    Column(modifier = Modifier.selectableGroup()) {
        radioOptions.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .selectable(
                        selected = (item == selectedOption),
                        onClick = { onOptionSelected(item) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (item == selectedOption),
                    colors = RadioButtonDefaults.colors(
                        unselectedColor = Ink, selectedColor = Salmon500
                    ),
                    onClick = null
                )
                Spacer(Modifier.padding(horizontal = 11.dp))
                //todo if selected option is the last, then enable textField
                Text(
                    text = if (radioOptions.indexOf(item) == radioOptions.lastIndex) "$item"
                    else "$item $unit"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsDetailScreenPreview() {
    SettingsDetailScreen(2)
}