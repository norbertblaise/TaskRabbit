package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType

class PomodoroViewModel : ViewModel() {
    val focusTime = 5000L
    val shortBreakTime = 2000L
    val longBreakTimer = 3000L
    val numberOfPoms = 4

    var currentTimeLeft by mutableStateOf(0L)
    var currentPom by mutableStateOf(1)

    var timerType = TimerType.INITIAL
    var timerState = TimerState.STOPPED
    var timerDuration = 80000L// todo get initial duratoin from the settings


    var timer = object : CountDownTimer(timerDuration, 1000) {
        override fun onFinish() {

        }

        override fun onTick(p0: Long) {
            //update label
            currentTimeLeft = p0 / 1000
        }
    }

    fun startStopButtonClicked() {
        when (timerState) {
            TimerState.STOPPED -> startTimer()//todo start timer
            TimerState.PAUSED -> startTimer()

            else -> {
                stopTimer()
            }
        }
    }

    fun startTimer() {
        //setup timer details
        setTimerDuration()
        timer.start()
    }

    fun stopTimer() {

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
        } else if (timerType == TimerType.SHORTBREAK){
            timerType = TimerType.FOCUS
            timerDuration = focusTime
        }else if (timerType == TimerType.LONGBREAK){
//            timerDuration =
        }

    }
}