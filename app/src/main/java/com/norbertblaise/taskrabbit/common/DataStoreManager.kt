package com.norbertblaise.taskrabbit.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.norbertblaise.taskrabbit.models.CurrentTimerValues
import com.norbertblaise.taskrabbit.models.SettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
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

val SETTINGS_DATASTORE = "settings"

//private const val FOCUS_TIME_DEFAULT = 19
//private const val SHORT_BREAK_TIME_DEFAULT = 25
//private const val LONG_BREAK_TIME_DEFAULT = 25
//private const val LONG_BREAK_INTERVAL_DEFAULT = 25
private const val FOCUS_TIME_DEFAULT = -1
private const val SHORT_BREAK_TIME_DEFAULT = -1
private const val LONG_BREAK_TIME_DEFAULT = -1
private const val LONG_BREAK_INTERVAL_DEFAULT = -1

data class UserSettings(
    val focusTime: Int,
    val shortBreak: Int,
    val longBreak: Int,
    val longBreakInterval: Int
)

class DataStoreManager(val context: Context) {


    companion object {
        private val Context.settingsPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "settings"
        )
        val FOCUS_TIME = intPreferencesKey("FOCUS_TIME")
        val SHORT_BREAK_TIME = intPreferencesKey("SHORT_BREAK_TIME")
        val LONG_BREAK_TIME = intPreferencesKey("LONG_BREAK_TIME")
        val NUM_OF_POMS = intPreferencesKey("NUM_OF_POMS")

        //running timer
        val TIMER_STATE = intPreferencesKey("TIMER_STATE")
        val TIMER_TYPE = intPreferencesKey("TIMER_TYPE")
        val APP_VISIBILITY = intPreferencesKey("APP_VISIBILITY")
        val CURRENT_TIME_LEFT = longPreferencesKey("CURRENT_TIME_LEFT")
        val CURRENT_POM = intPreferencesKey("CURRENT_POM")


        val ALARM_SET_TIME = longPreferencesKey("ALARM_SET_TIME")
        //flags
        val SERVICE_RUNNING_FLAG = booleanPreferencesKey("SERVICE_RUNNING_FLAG")
        val TIMER_VALUES_LOADED_FLAG = booleanPreferencesKey("TIMER_VALUES_LOADED_FLAG")

    }

    val timerPreferences: Flow<SettingsModel> = context.settingsPreferencesDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                logcat { exception.asLog() }
                emit(emptyPreferences())
            }
        }.map {
            SettingsModel(
                focusTime = it[FOCUS_TIME] ?: FOCUS_TIME_DEFAULT,
                shortBreak = it[SHORT_BREAK_TIME] ?: SHORT_BREAK_TIME_DEFAULT,
                longBreak = it[LONG_BREAK_TIME] ?: LONG_BREAK_TIME_DEFAULT,
                longBreakInterval = it[NUM_OF_POMS] ?: LONG_BREAK_INTERVAL_DEFAULT,

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
            it[FOCUS_TIME] = timeInMinutes

        }
    }

    suspend fun updateShortBreakTime(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[SHORT_BREAK_TIME] = timeInMinutes

        }
    }

    suspend fun updateLongBreakTime(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[LONG_BREAK_TIME] = timeInMinutes

        }
    }

    suspend fun updateLongBreakInterval(timeInMinutes: Int) {
        context.settingsPreferencesDataStore.edit {
            it[NUM_OF_POMS] = timeInMinutes

        }
    }

    suspend fun saveAlarmSetTime(currentTime: Long) {
        context.settingsPreferencesDataStore.edit {
            it[ALARM_SET_TIME] = currentTime
        }
    }

    suspend fun getAlarmSetTime(): Long {
        val alarmSetTime = context.settingsPreferencesDataStore.data.first()
        return alarmSetTime[ALARM_SET_TIME] ?: 0

    }

    suspend fun saveToDataStore(settings: SettingsModel) {
        context.settingsPreferencesDataStore.edit {
            it[FOCUS_TIME] = settings.focusTime
            it[SHORT_BREAK_TIME] = settings.shortBreak
            it[LONG_BREAK_TIME] = settings.longBreak
            it[NUM_OF_POMS] = settings.longBreakInterval

        }
    }

    suspend fun getFromDataStore() = context.settingsPreferencesDataStore.data.map {
        SettingsModel(
            focusTime = it[FOCUS_TIME] ?: 0,
            shortBreak = it[SHORT_BREAK_TIME] ?: 0,
            longBreak = it[LONG_BREAK_TIME] ?: 0,
            longBreakInterval = it[NUM_OF_POMS] ?: 0,

            )
    }

    suspend fun saveRunningParams(
        timeLeftInMillis: Long,
        timerType: Int,
        timerState: Int,
        appVisibility: Int,
        currentPom: Int

    ) {
        context.settingsPreferencesDataStore.edit {
            it[CURRENT_TIME_LEFT] = timeLeftInMillis
            it[TIMER_TYPE] = timerType
            it[TIMER_STATE] = timerState
            it[APP_VISIBILITY] = appVisibility
            it[CURRENT_POM] = currentPom

        }


    }

    suspend fun getBackgroundTimerParams() = context.settingsPreferencesDataStore.data.map {
        CurrentTimerValues(
            timeLeftInMillis = it[CURRENT_TIME_LEFT] ?: -0L,
            timerType = it[TIMER_TYPE] ?: -1,
            timerState = it[TIMER_STATE] ?: -1,
            appVisibility = it[APP_VISIBILITY] ?: -1,
            currentPom = it[CURRENT_POM] ?: -1,
        )
    }

    /**
     * saves the serviceRunningFlag
     */

    suspend fun saveServiceRunningFlag(serviceRunningFlag: Boolean) {
        context.settingsPreferencesDataStore.edit {
            it[SERVICE_RUNNING_FLAG] = serviceRunningFlag
        }
    }
    /**
     * observes the servieRunningFlag
     */
    suspend fun observeServiceRunningFlag(): Flow<Boolean> {
        return context.settingsPreferencesDataStore.data.map {
            it[SERVICE_RUNNING_FLAG] ?: false
        }
    }

    /**
     * saves the valuesLoadedFlag
     */

    suspend fun saveValuesLoadedFlag(valuesLoadedFlag: Boolean) {
        context.settingsPreferencesDataStore.edit {
            it[SERVICE_RUNNING_FLAG] = valuesLoadedFlag
        }
    }
    /**
     * observes the valuesLoadedFlag
     */
    suspend fun observeValuesLoadedFlag(): Flow<Boolean> {
        return context.settingsPreferencesDataStore.data.map {
            it[TIMER_VALUES_LOADED_FLAG] ?: false
        }
    }

}