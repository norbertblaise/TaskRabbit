package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType
import java.util.Timer

private const val TAG = "PomodoroViewModel"

class PomodoroViewModel : ViewModel() {
    var pomIsInitialized = false
    var focusTime = 5000L
    var shortBreakTime = 2000L
    var longBreakTimer = 3000L
    var numberOfPoms = 4

    var currentTimeLeft by mutableStateOf(0L)
    var currentPom by mutableStateOf(1)
    var startPauseButtonText by mutableStateOf("Start")

    var timerType = TimerType.INITIAL
    var timerState = TimerState.STOPPED
    var timerDuration = 8000L// todo get initial duration from the settings


    lateinit var timer: CountDownTimer

    fun startStopButtonClicked() {
        when (timerState) {
            TimerState.STOPPED -> {
                if (pomIsInitialized == false) {
                    initPomodoro()
                    startTimer()
                }else{
                    startTimer()
                }

            }//todo start timer
            TimerState.PAUSED ->{
                resumeTimer()
                TimerState.RUNNING
                setStartPauseButtonText()
            }

            else -> {
                stopTimer()
                setStartPauseButtonText()
            }
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
        setStartPauseButtonText()
    }

    /**
     * Stops any running timer and resets values
     */
    fun stopTimer() {
        timer.cancel()
        timerState = TimerState.PAUSED
    }

    /**
     * This stops any running timer but retains all variables
     */
    fun pauseTimer(){
        timer.cancel()
    }

    fun resumeTimer(){
        createTimer(currentTimeLeft)
        timer.start()
    }

    /**
     * creates a timer object with the specified duration
     */
    fun createTimer(duration: Long){
        timer = object : CountDownTimer(duration, 1000) {
            override fun onFinish() {

            }

            override fun onTick(p0: Long) {
                //update label
                currentTimeLeft = p0 / 1000
            }
        }
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
            timerDuration = longBreakTimer
        } else if (timerType == TimerType.SHORTBREAK) {
            timerType = TimerType.FOCUS
            timerDuration = focusTime
        } else if (timerType == TimerType.LONGBREAK) {
//            timerDuration =
        }

    }

    /**
     * This method sets up the pomodoro session, setting variables for focus time,
     * short and long break by getting these values that are stored in settings
     */
    fun initPomodoro() {
        //todo connect to settings mechanisim
        focusTime = 20000L
        Log.d(TAG, "initPomodoro: focusTime is set to $focusTime")
        shortBreakTime = 5000L
        longBreakTimer = 10000L
        numberOfPoms = 4
        currentPom = 1
        timerDuration = focusTime
        pomIsInitialized = true

    }

    /**
     * This changes the text of the start-pause button based on the TimerState
     */
    fun setStartPauseButtonText(){
        startPauseButtonText = when(timerState){
            TimerState.PAUSED -> "resume"
            TimerState.STOPPED -> "Start"
            TimerState.RUNNING -> "Pause"

        }
    }
}