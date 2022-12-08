package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PomodoroViewModel"

class PomodoroViewModel : ViewModel() {
    var pomIsInitialized = false
    var focusTime = 5000L
    var shortBreakTime = 2000L
    var longBreakTime = 3000L
    var numberOfPoms = 4

    var currentTimeLeft by mutableStateOf(0L)
    var currentPom by mutableStateOf(1)
    var startPauseButtonText by mutableStateOf("Start")
    var startPauseButtonIcon by mutableStateOf(R.drawable.ic_play_arrow)

    var timerType = TimerType.INITIAL
    var timerState = TimerState.STOPPED
    var timerDuration = 8000L// todo get initial duration from the settings


    lateinit var timer: CountDownTimer

    /**
     * This method sets up the pomodoro session, setting variables for focus time,
     * short and long break by getting these values that are stored in settings
     */
    fun initPomodoro() {
        //todo connect to settings mechanisim
        focusTime = 20000L
        Log.d(TAG, "initPomodoro: focusTime is set to $focusTime")
        shortBreakTime = 5000L
        longBreakTime = 10000L
        numberOfPoms = 4
        currentPom = 1
        timerDuration = focusTime
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
    fun stopTimer() {

        timer.cancel()
        timerState = TimerState.PAUSED
        setStartPauseButtonContents()
    }

    /**
     * This stops any running timer but retains all variables
     */
    fun pauseTimer() {

    }

    fun resumeTimer() {
        timer.cancel()
        Log.d(TAG, "resumeTimer: resuming with $currentTimeLeft time left")
        //convert currentTimeLeft from Seconds to Millis
        createTimer(currentTimeLeft*1000L)
        timer.start()
        timerState = TimerState.RUNNING
        setStartPauseButtonContents()
    }

    /**
     * creates a timer object with the specified duration
     */
    fun createTimer(duration: Long) {
        Log.d(TAG, "createTimer: timer created with $duration duration")
        timer = object : CountDownTimer(duration, 1000) {
            override fun onFinish() {
                timerState = TimerState.STOPPED
                setStartPauseButtonContents()
                nextTimer()
                startTimer()
            }

            override fun onTick(p0: Long) {
                //update label
                currentTimeLeft = p0 / 1000
                Log.d(TAG, "onTick: current time left is $currentTimeLeft")
            }
        }
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
        if (timerType == TimerType.INITIAL) {
            timerType = TimerType.FOCUS
            timerDuration = focusTime
        } else if (timerType == TimerType.FOCUS && currentPom == numberOfPoms) {
            timerType = TimerType.LONGBREAK
            timerDuration = longBreakTime
        } else if (timerType == TimerType.SHORTBREAK) {
            timerType = TimerType.FOCUS
            timerDuration = focusTime
        } else if (timerType == TimerType.LONGBREAK) {
//            timerDuration =
        }

    }


    /**
     * This changes the text of the start-pause button based on the TimerState
     */
    fun setStartPauseButtonText() {
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
    fun setStartPauseButtonIcon() {
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
    fun nextTimer() {
        when (timerType) {
            TimerType.FOCUS -> {
                timerType = if (currentPom < numberOfPoms) {
                    TimerType.SHORTBREAK
                } else {
                    TimerType.LONGBREAK
                }
            }
            TimerType.SHORTBREAK -> {
                timerType = TimerType.FOCUS
                currentPom++
            }
            TimerType.LONGBREAK -> timerType = TimerType.INITIAL
        }
    }
}