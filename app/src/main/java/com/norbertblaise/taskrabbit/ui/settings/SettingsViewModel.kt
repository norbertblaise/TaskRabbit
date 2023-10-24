package com.norbertblaise.taskrabbit.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.models.SettingsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStoreManager: DataStoreManager? = null) : ViewModel() {

    private val timerPreferences = dataStoreManager?.timerPreferences
    lateinit var settings: SettingsModel

    private var _focusTime = MutableLiveData<Int>()
    val focusTime: LiveData<Int>
        get() = _focusTime
    private var _shortBreakTime = MutableLiveData<Int>()
    val shortBreakTime: LiveData<Int>
        get() = _shortBreakTime

    private var _longBreakTime = MutableLiveData<Int>()
    val longBreakTime: LiveData<Int>
        get() = _longBreakTime

    private var _longBreakInterval = MutableLiveData<Int>()
    val longBreakInterval: LiveData<Int>
        get() = _longBreakInterval

    //Flow variables
    val focusTimeFlow = MutableStateFlow<Int>(0)
    val shortBreakFlow = MutableStateFlow(0)
    val longBreakFlow = MutableStateFlow(0)
    val longBreakIntervalFlow = MutableStateFlow(0)

    // TODO: add values from datastore init block
    init {
        viewModelScope.launch {
            dataStoreManager?.getFromDataStore()?.catch { e ->
                e.printStackTrace()
            }?.collect {
                _focusTime.value = it.focusTime
                _shortBreakTime.value = it.shortBreak
                _longBreakTime.value = it.longBreak
                _longBreakInterval.value = it.longBreakInterval

                focusTimeFlow.value = it.focusTime
                shortBreakFlow.value = it.shortBreak
                longBreakFlow.value = it.longBreak
                longBreakIntervalFlow.value = it.longBreakInterval

            }
        }
    }


}