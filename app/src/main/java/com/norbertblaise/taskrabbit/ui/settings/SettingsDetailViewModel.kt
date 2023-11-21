package com.norbertblaise.taskrabbit.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.models.SettingsModel
import com.norbertblaise.taskrabbit.models.TimerSettingsParameter
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsDetailViewModel(
    private val dataStoreManager: DataStoreManager,
    private val arg: Int,
    private val context: Context
) : ViewModel() {
    private val TAG = "SettingsDetailViewModel"
    val CUSTOM = "Custom"
    var focusTime = 0
    var shortBreak = 0
    var longBreak = 0
    var longBreakInterval = -1
    var settingIsCustom = false
    var initialCheckOption = "0"
    var settingBasedOnArg = ""

    val focusTimeOptions = listOf("10", "25", "55", "90", "Custom")
    val shortBreakOptions = listOf("5", "10", "15", "20", "Custom")
    val longBreakOptions = listOf("20", "30", "40", "50", "Custom")
    val longBreakIntervalOptions = listOf("4", "6", "8", "Custom")

    val timerSettingsParameter: TimerSettingsParameter

    //map int to
    var radioOptions = listOf<String>()
    var selectedOption by mutableStateOf("")
    var textValue by mutableStateOf("")

    var settings: MutableLiveData<SettingsModel> = MutableLiveData()
    var settingToStore = 0

    init {
        timerSettingsParameter = mapIntToTimerSettingsParameter(arg)

        radioOptions = when (timerSettingsParameter) {
            TimerSettingsParameter.FOCUS_TIME -> focusTimeOptions
            TimerSettingsParameter.SHORT_BREAK -> shortBreakOptions
            TimerSettingsParameter.LONG_BREAK -> longBreakOptions
            TimerSettingsParameter.LONG_BREAK_INTERVAL -> longBreakIntervalOptions
        }

        viewModelScope.launch {
            dataStoreManager.getFromDataStore().collect {
                focusTime = (it.focusTime)
                shortBreak = (it.shortBreak)
                longBreak = (it.longBreak)
                longBreakInterval = it.longBreakInterval
                settings.postValue(it)
            }
        }
        getSettingBasedOnArg()
        setInitialSettingValue(settingBasedOnArg, radioOptions)
        selectedOption = initialCheckOption
        if (selectedOption == CUSTOM) {
            textValue = settingBasedOnArg
        }
        Timber.tag(TAG).d("settingBasedOnArg is: %s", settingBasedOnArg)
    }

    private fun getSettingBasedOnArg() {
        settingBasedOnArg = when (timerSettingsParameter) {
            TimerSettingsParameter.FOCUS_TIME -> focusTime.toString()
            TimerSettingsParameter.SHORT_BREAK -> shortBreak.toString()
            TimerSettingsParameter.LONG_BREAK -> longBreak.toString()
            TimerSettingsParameter.LONG_BREAK_INTERVAL -> longBreakInterval.toString()
        }
    }

    fun mapIntToTimerSettingsParameter(value: Int): TimerSettingsParameter {
        return when (value) {
            0 -> TimerSettingsParameter.FOCUS_TIME
            1 -> TimerSettingsParameter.SHORT_BREAK
            2 -> TimerSettingsParameter.LONG_BREAK
            3 -> TimerSettingsParameter.LONG_BREAK_INTERVAL
            else -> throw error("only int 0 - 3 are valid params")
        }
    }

    /**
     * Saves the new settings to the dataStore
     */
    fun updateSettings() {
        Timber.tag(TAG).d("updateSettings: textValue is %s", textValue)

        if (selectedOption != CUSTOM) {
            settingToStore = selectedOption.toInt()
        } else {
            settingToStore = textValue.toInt()
        }
        when (timerSettingsParameter) {
            TimerSettingsParameter.FOCUS_TIME -> updateFocusTime(settingToStore)
            TimerSettingsParameter.SHORT_BREAK -> updateShortBreakTime(settingToStore)
            TimerSettingsParameter.LONG_BREAK -> updateLongBreakTime(settingToStore)
            TimerSettingsParameter.LONG_BREAK_INTERVAL -> updateLongBreakInterval(settingToStore)
        }
        Toast.makeText(context, "Setting updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateFocusTime(timeInMinutes: Int) {
        viewModelScope.launch {
            val settingsModel = SettingsModel(
                focusTime = timeInMinutes,
                shortBreak = shortBreak,
                longBreak = longBreak,
                longBreakInterval = longBreakInterval
            )
            dataStoreManager.saveToDataStore(settingsModel)
        }
    }

    fun isCustomTextValid(): Boolean {
        return !(textValue.isNotEmpty() && !isValidText(textValue))
    }

    /**
     * Checks if input string is a positive whole number
     */
    private fun isValidText(text: String): Boolean {
        // Add your custom validation rules here
        return text.matches(Regex("^\\d+$"))
    }

    /**
     * sets the initial radio option based on the arg received and the value gotten from the dataStore
     */
    private fun setInitialSettingValue(settingBasedOnArg: String, radioOptions: List<String>) {
        //check if the settingBased on arg matches the options list otherwise set initialOption to "CUSTOM"
        val optionArr = radioOptions.filter { item -> item == settingBasedOnArg }
        if (optionArr.isNotEmpty()) {
            //set selection option to the single value in the array
            initialCheckOption = settingBasedOnArg
            Timber.tag(TAG).d("setInitialSettingValue: selectedOption is %s", selectedOption)
        } else {
            Timber.tag(TAG).d("setInitialSettingValue: else block called")
            settingIsCustom = true
            initialCheckOption = CUSTOM
            textValue = settingBasedOnArg
            Timber.tag(TAG).d("setInitialSettingValue: selectedOption is %s", selectedOption)
        }
    }

    private fun updateShortBreakTime(timeInMinutes: Int) {
        viewModelScope.launch {
            dataStoreManager.updateShortBreakTime(timeInMinutes)
        }
    }

    private fun updateLongBreakTime(timeInMinutes: Int) {
        viewModelScope.launch {
            dataStoreManager.updateLongBreakTime(timeInMinutes)
        }
    }

    private fun updateLongBreakInterval(timeInMinutes: Int) {
        viewModelScope.launch {
            dataStoreManager.updateLongBreakInterval(timeInMinutes)
        }
    }
}