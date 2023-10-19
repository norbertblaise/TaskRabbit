package com.norbertblaise.taskrabbit.models

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.norbertblaise.taskrabbit.ui.pomodoro.Pomodoro


class SettingsManager(val context: Context) {
    private val Context.datastore by preferencesDataStore("settings_prefs")

    private val datastore = context.datastore

    companion object {
        val WORK_LENGTH_KEY = longPreferencesKey("WORK_LENGTH")
        val SHORT_BREAK_KEY = longPreferencesKey("SHORT_BREAK_LENGTH")
        val LONG_BREAK_KEY = longPreferencesKey("LONG_BREAK_LENGTH")
        val LONG_BREAK_INTERVAL_KEY = intPreferencesKey("LONG_BREAK_INTERVAL")
    }

    suspend fun saveToDatastore(pomodoro: Pomodoro) {
        context.datastore.edit {
            it[WORK_LENGTH_KEY] = pomodoro.workLength
            it[SHORT_BREAK_KEY] = pomodoro.shortBreakDuration
            it[LONG_BREAK_KEY] = pomodoro.longBreakDuration
            it[LONG_BREAK_INTERVAL_KEY] = pomodoro.longBreakInterval
        }
    }

    suspend fun getFromDataStore(){
        context.datastore.edit {
            Pomodoro(
                workLength = it[WORK_LENGTH_KEY]?: 0,
                shortBreakDuration = it[SHORT_BREAK_KEY]?: 0,
                longBreakDuration = it[LONG_BREAK_KEY]?: 0,
                longBreakInterval = it[LONG_BREAK_INTERVAL_KEY]?:0
            )
        }
    }
}