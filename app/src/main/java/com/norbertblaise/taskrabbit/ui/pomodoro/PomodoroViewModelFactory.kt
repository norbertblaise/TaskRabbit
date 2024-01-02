package com.norbertblaise.taskrabbit.ui.pomodoro

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.repository.SettingsRepositoryImpl

class PomodoroViewModelFactory(
    private val dataStoreManager: DataStoreManager,
    private val settingsRepositoryImpl: SettingsRepositoryImpl,
    private val lifecycleOwner: LifecycleOwner
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PomodoroViewModel(dataStoreManager, settingsRepositoryImpl, lifecycleOwner) as T
}