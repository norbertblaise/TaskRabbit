package com.norbertblaise.taskrabbit.ui.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.repository.SettingsRepositoryImpl

class PomodoroViewModelFactory(
    private val dataStoreManager: DataStoreManager,
    private val settingsRepositoryImpl: SettingsRepositoryImpl
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PomodoroViewModel(dataStoreManager, settingsRepositoryImpl) as T
}