package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType
import com.norbertblaise.taskrabbit.ui.theme.DarkBlue
import com.norbertblaise.taskrabbit.ui.theme.DarkCyan
import com.norbertblaise.taskrabbit.ui.theme.Navy
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import timber.log.Timber

private const val TAG = "PomodoroViewModel"

class PomodoroViewModel : ViewModel() {
    var pomIsInitialized = false
    var focusTime = 5000L
    var shortBreakTime = 2000L
    var longBreakTime = 3000L
    var numberOfPoms = 4

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


    /**
     * This method sets up the pomodoro session, setting variables for focus time,
     * short and long break by getting these values that are stored in settings
     */
    private fun initPomodoro() {
        //todo connect to settings mechanisim
        focusTime = 20000L
        Timber.d("$TAG initPomodoro: focusTime is set to $focusTime")
        shortBreakTime = 5000L
        longBreakTime = 10000L
        numberOfPoms = 4
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
        createTimer(currentTimeLeftInMillis * 1000L)
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
                    if (currentPom <= numberOfPoms) {
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

                timerType = if (currentPom % numberOfPoms == 0) {
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

    /**
     * Calculates the time left in percent
     */
    fun calculatePercentageTimeLeft() {
        currentTimeLeftInPercentage = currentTimeLeftInMillis.toFloat() / (timerDuration)
    }

    fun updateTimeLeftEveryMilli() {

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