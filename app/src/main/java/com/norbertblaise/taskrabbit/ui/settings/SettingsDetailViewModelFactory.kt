package com.norbertblaise.taskrabbit.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.norbertblaise.taskrabbit.common.DataStoreManager

class SettingsDetailViewModelFactory(
    private val dataStoreManager: DataStoreManager,
    private val arg: Int,
    private val context: Context
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SettingsDetailViewModel(dataStoreManager, arg, context) as T
}