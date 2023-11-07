package com.norbertblaise.taskrabbit.repository

import com.norbertblaise.taskrabbit.models.SettingsModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    suspend fun saveSettings(settings: SettingsModel)

    suspend fun getSettings(): Flow<SettingsModel>
}