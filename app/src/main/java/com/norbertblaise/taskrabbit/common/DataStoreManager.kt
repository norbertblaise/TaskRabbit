package com.norbertblaise.taskrabbit.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.norbertblaise.taskrabbit.models.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import logcat.asLog
import logcat.logcat
import java.io.IOException

data class TimerPreferences(
    val focusTimeInMinutes: Int,
    val shortBreakTimeInMinutes: Int,
    val longBreakTimeInMinutes: Int,
    val longBreakIntervalInMinutes: Int,

    )

private const val FOCUS_TIME_DEFAULT = 25
private const val SHORT_BREAK_TIME_DEFAULT = 25
private const val LONG_BREAK_TIME_DEFAULT = 25
private const val LONG_BREAK_INTERVAL_DEFAULT = 25


class DataStoreManager(val context: Context) {
    private val Context.settingsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "settings"
    )

    companion object {
        val FOCUS_TIME = intPreferencesKey("FOCUS_TIME")
        val SHORT_BREAK_TIME = intPreferencesKey("SHORT_BREAK_TIME")
        val LONG_BREAK_TIME = intPreferencesKey("LONG_BREAK_TIME")
        val NUM_OF_POMS = intPreferencesKey("NUM_OF_POMS")
    }

    val timerPreferences: Flow<Settings> = context.settingsPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                logcat { exception.asLog() }
                emit(emptyPreferences())
            }
        }.map {
            Settings(
                focusTime = it[PreferencesKeys.FOCUS_TIME] ?: FOCUS_TIME_DEFAULT,
                shortBreak = it[PreferencesKeys.SHORT_BREAK_TIME] ?: SHORT_BREAK_TIME_DEFAULT,
                longBreak = it[PreferencesKeys.LONG_BREAK_TIME] ?: LONG_BREAK_TIME_DEFAULT,
                longBreakInterval = it[PreferencesKeys.NUM_OF_POMS] ?: LONG_BREAK_INTERVAL_DEFAULT,

                )
        }

    private object PreferencesKeys {

        val FOCUS_TIME = intPreferencesKey("FOCUS_TIME")
        val SHORT_BREAK_TIME = intPreferencesKey("SHORT_BREAK_TIME")
        val LONG_BREAK_TIME = intPreferencesKey("LONG_BREAK_TIME")
        val NUM_OF_POMS = intPreferencesKey("NUM_OF_POMS")
    }

    suspend fun updateFocusTime(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[PreferencesKeys.FOCUS_TIME] = timeInMinutes

        }
    }

    suspend fun updateShortBreakTime(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[PreferencesKeys.SHORT_BREAK_TIME] = timeInMinutes

        }
    }

    suspend fun updateLongBreakTime(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[PreferencesKeys.LONG_BREAK_TIME] = timeInMinutes

        }
    }

    suspend fun updateLongBreakInterval(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[PreferencesKeys.NUM_OF_POMS] = timeInMinutes

        }
    }

    suspend fun saveToDataStore(settings: Settings) {
        context.settingsPreferencesDataStore.edit {
            it[FOCUS_TIME] = settings.focusTime
            it[SHORT_BREAK_TIME] = settings.shortBreak
            it[LONG_BREAK_TIME] = settings.longBreak
            it[NUM_OF_POMS] = settings.longBreakInterval

        }
    }

    suspend fun getFromDataStore() = context.settingsPreferencesDataStore.data.map {
        Settings(
            focusTime = it[FOCUS_TIME] ?: 0,
            shortBreak = it[SHORT_BREAK_TIME] ?: 0,
            longBreak = it[LONG_BREAK_TIME] ?: 0,
            longBreakInterval = it[NUM_OF_POMS] ?: 0,

            )
    }

}