package com.norbertblaise.taskrabbit.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.common.descriptionsList
import com.norbertblaise.taskrabbit.common.settingParameter
import com.norbertblaise.taskrabbit.models.TimerSettingsParameter
import com.norbertblaise.taskrabbit.ui.components.SettingsTopAppBar
import com.norbertblaise.taskrabbit.ui.theme.Charcoal
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon100
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import timber.log.Timber


private const val TAG = "SettingsDetailScreen"


@Composable
fun SettingsDetailScreen(
//    settingsViewModel: SettingsViewModel = viewModel(),
    arg: Int,
    onUpButtonClicked: () -> Unit

) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val viewModel: SettingsDetailViewModel =
        viewModel(factory = SettingsDetailViewModelFactory(dataStoreManager, arg, context))

    val timerSettingsParameter = viewModel.mapIntToTimerSettingsParameter(arg)

    val radioOptions = viewModel.radioOptions

    val selectedOption = viewModel.selectedOption
    Timber.tag(TAG).d("SettingsDetailScreen: selectedOption is %s", selectedOption)
    val onOptionSelected = viewModel.selectedOption

//    var textValue by remember {
//        mutableStateOf(TextFieldValue(""))
//    }
    var textValue = viewModel.textValue
    val unit = if (timerSettingsParameter == TimerSettingsParameter.LONG_BREAK_INTERVAL) "Pomos"
    else "min"


    Scaffold(
        modifier = Modifier.padding(horizontal = 16.dp),
        topBar = {
            SettingsTopAppBar(
                title = settingParameter[timerSettingsParameter.type],
                onUpButtonClicked = onUpButtonClicked
            )

        }) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = descriptionsList[timerSettingsParameter.type],
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Charcoal
            )
            Spacer(Modifier.height(14.dp))
            SettingsOptions(
                unit = unit, radioOptions = radioOptions,
                selectedOption

            ) {
                viewModel.selectedOption = it
            }
            Spacer(Modifier.height(16.dp))
            TextField(
                enabled = (viewModel.selectedOption == viewModel.CUSTOM),
                value = viewModel.textValue,
                isError = viewModel.isCustomTextValid(),
                onValueChange = {
                    viewModel.textValue = it
                },
                label = { Text(unit)},

                // TODO: add field validation, it should only accept whole numbers and
                //  must not be empty when the custom option is chosen

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        //todo go back to SettingsScreen.kt
                              onUpButtonClicked
                    },
                    border = BorderStroke(1.5.dp, Salmon500),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Salmon500,
                        backgroundColor = Color.Transparent
                    )
                ) {
                    Text(text = "CANCEL")
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Button(
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        when (selectedOption) {
                            viewModel.CUSTOM -> if (viewModel.isCustomTextValid()) {
                                viewModel.updateSettings()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter Whole numbers only",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else -> viewModel.updateSettings()
                        }


                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Salmon500,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "SAVE")
                }
            }

        }
    }
}

//@Composable
//fun SettingsOptions(unit: String, radioOptions: List<Any>) {
//    //todo hoist this state to viewModel
//    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) } //todo get default value from stored value
//
//    Column(modifier = Modifier.selectableGroup()) {
//        radioOptions.forEach { item ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(32.dp)
//                    .selectable(
//                        selected = (item == selectedOption),
//                        onClick = { onOptionSelected(item) },
//                        role = Role.RadioButton
//                    ),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                RadioButton(
//                    selected = (item == selectedOption),
//                    colors = RadioButtonDefaults.colors(
//                        unselectedColor = Ink, selectedColor = Salmon500
//                    ),
//                    onClick = null
//                )
//                Spacer(Modifier.padding(horizontal = 11.dp))
//                //todo if selected option is the last, then enable textField
//                Text(
//                    text = if (radioOptions.indexOf(item) == radioOptions.lastIndex) "$item"
//                    else "$item $unit"
//                )
//            }
//
//        }
//    }
//}

@Composable
fun SettingsOptions(
    unit: String, radioOptions: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    //todo hoist this state to viewModel
//    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) } //todo get default value from stored value

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
                Spacer(Modifier.padding(horizontal = 8.dp))
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
    SettingsDetailScreen(
//        settingsViewModel = viewModel(),
        2, onUpButtonClicked = {/*Do nothing*/ })
}