package com.norbertblaise.taskrabbit.ui.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.models.SettingsModel
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {
    private val TAG = "SettingsViewModel"
    private var isDataStoreConnected = false
    var focusTime = 0
    var shortBreak = 0
    var longBreak = 0
    var longBreakInterval = -1

    var settings: MutableLiveData<SettingsModel> = MutableLiveData()

    // TODO: add values from datastore init block
    init {
        Timber.tag(TAG).d("Initblock called: ")
        viewModelScope.launch {
            dataStoreManager.getFromDataStore().collect{
                focusTime = (it.focusTime)
                shortBreak = (it.shortBreak)
                longBreak = (it.longBreak)
                longBreakInterval = it.longBreakInterval
                settings.postValue(it)
            }
        }

    }


    private fun updateSettings(){

    }
}