package com.norbertblaise.taskrabbit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.norbertblaise.taskrabbit.common.DataStoreManager

class SettingsViewModelFactory(private val dataStoreManager: DataStoreManager) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SettingsViewModel(dataStoreManager) as T
}