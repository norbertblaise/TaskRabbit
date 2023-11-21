package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType
import com.norbertblaise.taskrabbit.models.SettingsModel
import com.norbertblaise.taskrabbit.repository.SettingsRepositoryImpl
import com.norbertblaise.taskrabbit.ui.theme.DarkCyan
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "PomodoroViewModel"

class PomodoroViewModel(
    private val dataStoreManager: DataStoreManager? = null,
    private val settingsRepositoryImpl: SettingsRepositoryImpl
) : ViewModel() {
    private val timerPreferences = dataStoreManager?.timerPreferences

    var settingsFlow = timerPreferences
    var pomIsInitialized = false

    //    var focusTime = focusTimeFlow
//    var shortBreakTime = timerPreferences.map { it.shortBreak }
//    var longBreakTime = timerPreferences.map { it.longBreak }
//    var numberOfPoms = timerPreferences.map { it.longBreakInterval }
    var focusTime = 5000L
    var shortBreakTime = 2000L
    var longBreakTime = 3000L
    var longBreakInterval = 4
    var settings: SettingsModel

    var focusTimeLiveData: MutableLiveData<Int> = MutableLiveData(0)
    var shortBreakTimeLiveData: MutableLiveData<Int> = MutableLiveData(0)
    var longBreakTimeLiveDate: MutableLiveData<Int> = MutableLiveData(0)
    var longBreakIntervalLiveData: MutableLiveData<Int> = MutableLiveData(0)
    var settingsModelLiveData: MutableLiveData<SettingsModel> = MutableLiveData()

    var indicatorColour by mutableStateOf(Salmon500)
    var currentTimeLeftInMillis by mutableStateOf(0L)
    var currentPom by mutableStateOf(1)
    var timerLabel by mutableStateOf("Focus")
    var startPauseButtonText by mutableStateOf("Start")
    var startPauseButtonIcon by mutableStateOf(R.drawable.ic_play_arrow)

    var timerType = TimerType.INITIAL
    var timerState = TimerState.STOPPED
    var timerDuration = 8000L// todo get initial duration from the settings
    var currentTimeLeftInPercentage by mutableStateOf(1.0f)
    var currentTimeLeftInSeconds by mutableStateOf(0L)
    lateinit var timer: CountDownTimer

    var showDialog by mutableStateOf(false)

    init {

        settings = SettingsModel()

        viewModelScope.launch {
            dataStoreManager!!.getFromDataStore().collect{
                focusTime = (it.focusTime) *60 *1000.toLong()
                shortBreakTime = (it.shortBreak) *60 *1000.toLong()
                longBreakTime = (it.longBreak) *60 *1000.toLong()
                longBreakInterval = it.longBreakInterval
            }
        }
        initPomodoro()

    }

    fun getSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepositoryImpl.getSettings().collect {
                settingsModelLiveData.postValue(it)
            }
        }
    }

    fun saveSettings(settings: SettingsModel) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepositoryImpl.saveSettings(settings)
        }
    }

    /**
     * This method sets up the pomodoro session, setting variables for focus time,
     * short and long break by getting these values that are stored in settings
     */
    private fun initPomodoro() {
        Log.d(TAG, "initPomodoro called")
        var focusTimeTest = 0
        var shortBreakTimeTest = 0
        //first check if datastore has data if not push new settings object
        viewModelScope.launch {
            dataStoreManager?.getFromDataStore()?.catch { e ->
                e.printStackTrace()
            }?.collect {
                focusTimeTest = it.focusTime
                shortBreakTimeTest = it.shortBreak
                Timber.tag(TAG).d("initPomodoro: focusTimeTest value is: %s", focusTimeTest)
                Timber.tag(TAG).d("initPomodoro: shortBreakTimeTest value is: %s", shortBreakTimeTest)
            }
            if (focusTimeTest == 0) {
                viewModelScope.launch {

                    dataStoreManager?.saveToDataStore(settings)
                    Timber.tag(TAG).d("initPomodoro: settings saved to datastore it %s", settings)
                    Timber.tag(TAG)
                        .d("initPomodoro: focustime  sent to Datastore is: %s", settings.focusTime)
                    Timber.tag(TAG)
                        .d("initPomodoro: shortbreaktime  sent to Datastore is: %s", settings.shortBreak)

                }.invokeOnCompletion {
                    loadPomValues()
                }

            }else {
                Log.d(TAG, "initPomodoro: else block called")
                //todo connect to settings mechanisim
                viewModelScope.launch(Dispatchers.IO) {
                    dataStoreManager?.getFromDataStore()?.catch { e->
                        e.printStackTrace()
                    }?.collect{
                        focusTime = (it.focusTime) *60 *1000L
                        shortBreakTime = (it.shortBreak) *60 *1000L
                    }
                }
            }
        }.invokeOnCompletion {

        }

    }

    private fun loadPomValues() {
        focusTime = (settings.focusTime * 1000 * 60).toLong()
        Timber.d("$TAG initPomodoro: focusTime is set to $focusTime")
        shortBreakTime = (settings.shortBreak * 1000 * 60).toLong()
        longBreakTime = (settings.longBreak * 1000 * 60).toLong()
        longBreakInterval = settings.longBreakInterval
        currentPom = 1
        timerDuration = focusTime
        timerLabel = "Focus"
        pomIsInitialized = true
    }


    /**
     * Click handler for startStopButton
     */
    fun onStartStopButtonClick() {
        when (timerState) {
            TimerState.STOPPED -> {
                if (pomIsInitialized == false) {
                    initPomodoro()
                    //set the Timer type to Focus
                    timerType = TimerType.FOCUS
                    startTimer()
                } else {
                    startTimer()
                }
            }//todo start timer
            TimerState.PAUSED -> {
                resumeTimer()
                TimerState.RUNNING
                setStartPauseButtonContents()
//                CoroutineScope(Dispatchers.Default).launch {  }
            }

            else -> {
                stopTimer()
                setStartPauseButtonContents()
            }
        }
    }

    /**
     * Handles clicks on reset Timer button
     */
    fun onResetButtonClick() {

        when (timerState) {
            TimerState.PAUSED -> {
                resetCurrentTimer()
            }

            TimerState.RUNNING -> {
                stopTimer()
                resetCurrentTimer()
                startTimer()
            }

            TimerState.STOPPED -> {
                //do nothing
            }

            else -> {
                //no-op
            }
        }
    }

    /**
     * Resets current timer back to Timer value cannot reset shortBreak and longBreak timers
     */
    private fun resetCurrentTimer() {
        if (timerType == TimerType.FOCUS) {
            timer.cancel()
            setTimerDuration()
            createTimer(timerDuration)
        }
    }

    /**
     * Starts a new timer of full length
     */
    fun startTimer() {
        //setup timer details
        setTimerDuration()
        createTimer(timerDuration)
        timer.start()
        timerState = TimerState.RUNNING
        setStartPauseButtonContents()
    }

    /**
     * Stops any running timer and resets values
     */
    private fun stopTimer() {
        timer.cancel()
        timerState = TimerState.PAUSED
        setStartPauseButtonContents()
    }

    /**
     * This stops any running timer but retains all variables
     */
    fun pauseTimer() {

    }

    private fun resumeTimer() {
        timer.cancel()
        Timber.d("$TAG resumeTimer: resuming with $currentTimeLeftInMillis time left")
        //convert currentTimeLeft from Seconds to Millis
        createTimer(currentTimeLeftInMillis)
        timer.start()
        timerState = TimerState.RUNNING
        setStartPauseButtonContents()
    }

    /**
     * creates a timer object with the specified duration
     */
    private fun createTimer(duration: Long) {
        Timber.d("$TAG createTimer: timer created with $duration duration")
        timer = object : CountDownTimer(duration, 1) {
            override fun onFinish() {
                timerState = TimerState.STOPPED
                setStartPauseButtonContents()
                if (timerType == TimerType.LONGBREAK) {
                    //stop the timer
                    resetPomodoro()
                } else
                    if (currentPom <= longBreakInterval) {
                        nextTimer()
                        setTimerLabel()
                        setIndicatorColor()
                        startTimer()
                    }
            }

            override fun onTick(p0: Long) {
                //update label
                currentTimeLeftInMillis = p0
                //update time left in seconds every 1000 milliseconds
                if (p0 % 1000 == 0L) {
                    currentTimeLeftInSeconds = currentTimeLeftInMillis / 1000
                    Timber.d("onTick: currentTimeLeftInSeconds is $currentTimeLeftInSeconds")
                }
                calculatePercentageTimeLeft()

            }
        }
    }

    /**
     * resets pomodoro values to original and resets UI values to initial
     */
    private fun resetPomodoro() {
        timer.cancel()
        timerType = TimerType.INITIAL
        initPomodoro()
    }

    /**
     * Sets both button icon and texts based on TimerState
     */
    private fun setStartPauseButtonContents() {
        setStartPauseButtonIcon()
        setStartPauseButtonText()
    }

    /**
     * If the current timerType is TimerType.INITIAL then set timerDuration to focus duration and timer type to FOCUS
     * If the number of poms left is less than 2 ie final pom and the current timer type is focus time,
     * then set the duration to long break duration.
     *
     */
    private fun setTimerDuration() {
        Timber.d("$TAG setTimerDuration: called")
        when (timerType) {
            TimerType.FOCUS -> timerDuration = focusTime
            TimerType.SHORTBREAK -> timerDuration = shortBreakTime
            TimerType.LONGBREAK -> timerDuration = longBreakTime
            else -> {
                //no-op
            }
        }
        Timber.d("$TAG setTimerDuration: timer duration is: $timerDuration")
    }


    /**
     * This changes the text of the start-pause button based on the TimerState
     */
    private fun setStartPauseButtonText() {
        startPauseButtonText = when (timerState) {
            TimerState.PAUSED -> "Resume"
            TimerState.STOPPED -> "Start"
            TimerState.RUNNING -> "Pause"
            TimerState.INITIAL -> "Start"


        }
    }

    /**
     * This changes the text of the startpause button icon based on the TimerState
     */
    private fun setStartPauseButtonIcon() {
        startPauseButtonIcon = when (timerState) {
            TimerState.PAUSED -> R.drawable.ic_play_arrow
            TimerState.STOPPED -> R.drawable.ic_play_arrow
            TimerState.RUNNING -> R.drawable.ic_pause
            TimerState.INITIAL -> R.drawable.ic_play_arrow

        }
    }

    /**
     * Advance to the next timer until long rest
     */
    private fun nextTimer() {
        Timber.d("$TAG nextTimer: Called")
        when (timerType) {
            TimerType.FOCUS -> {

                timerType = if (currentPom % longBreakInterval == 0) {
                    TimerType.LONGBREAK
                } else {
                    TimerType.SHORTBREAK
                }
            }

            TimerType.SHORTBREAK -> {
                currentPom++
                timerType = TimerType.FOCUS
            }

            TimerType.LONGBREAK -> {
                timerType = TimerType.INITIAL


            }

            else -> {
                //no-op
            }
        }
        Timber.d("$TAG nextTimer: current timer type is $timerType")
    }

    /**
     * Configures the label of a timer depending on the timer type
     */
    fun setTimerLabel() {
        Timber.d("$TAG setTimerLabel: called")
        when (timerType) {
            TimerType.FOCUS -> timerLabel = "Focus"
            TimerType.SHORTBREAK -> timerLabel = "Short Break"
            TimerType.LONGBREAK -> timerLabel = "Long Break"
            else -> {
                //no-op
            }
        }
    }

    /**
     * Handles clicks on skip button. It only allows Breaks to be skipped. it displays a toast to tell the user this
     * and will prompt the user if they really want to skip a break by displaying an AlertDialog
     */
    fun onSkipButtonClick() {
        showDialog = true
    }

    /**
     * Handles confirm button press
     */
    fun onDialogConfirm() {
        showDialog = false
        //skip current timer
        if (timerType == TimerType.SHORTBREAK || timerType == TimerType.LONGBREAK) {
            timer.onFinish()
        }
    }

    /**
     * Dismisses Skip Rest Dialog
     */
    fun onDialogDismiss() {
        showDialog = false
    }

    override fun onCleared() {
        super.onCleared()
        pomIsInitialized = false
    }

    /**
     * Calculates the time left in percent
     */
    fun calculatePercentageTimeLeft() {
        currentTimeLeftInPercentage = currentTimeLeftInMillis.toFloat() / (timerDuration)
    }


    /**
     * Sets progress indicator color based on the TimerType
     */
    fun setIndicatorColor() {
        indicatorColour = when (timerType) {
            TimerType.FOCUS -> Salmon500
            TimerType.INITIAL -> Salmon500
            TimerType.SHORTBREAK -> DarkCyan
            TimerType.LONGBREAK -> DarkCyan
        }
    }

}