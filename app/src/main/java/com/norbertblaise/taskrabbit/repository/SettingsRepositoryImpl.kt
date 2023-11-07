package com.norbertblaise.taskrabbit.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.norbertblaise.taskrabbit.models.SettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val DATASTORE_NAME = "settings_datastore"
val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    companion object {
        val FOCUS_TIME = intPreferencesKey("FOCUS_TIME")
        val SHORT_BREAK_TIME = intPreferencesKey("SHORT_BREAK_TIME")
        val LONG_BREAK_TIME = intPreferencesKey("LONG_BREAK_TIME")
        val LONG_BREAK_INTERVAL = intPreferencesKey("NUM_OF_POMS")
    }

    override suspend fun saveSettings(settings: SettingsModel) {
        context.datastore.edit {
            it[FOCUS_TIME] = settings.focusTime
            it[SHORT_BREAK_TIME] = settings.shortBreak
            it[LONG_BREAK_TIME] = settings.longBreak
            it[LONG_BREAK_INTERVAL] = settings.longBreakInterval

        }
    }

    override suspend fun getSettings(): Flow<SettingsModel> = context.datastore.data.map {
        SettingsModel(
            focusTime = it[FOCUS_TIME]!!,
            shortBreak = it[SHORT_BREAK_TIME]!!,
            longBreak = it[LONG_BREAK_TIME]!!,
            longBreakInterval = it[LONG_BREAK_INTERVAL]!!
        )
    }


}