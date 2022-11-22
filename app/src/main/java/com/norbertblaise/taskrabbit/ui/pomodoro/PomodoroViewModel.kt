package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PomodoroViewModel : ViewModel() {
    val focusTime = 70000L
    val shortBreakTime = 2000L
    val longBreakTimer = 3000L
    val numberOfPoms = 4
    var currentTimeLeft by mutableStateOf(0L)
    var timerType by mutableStateOf("Focus")
    var currentPom by mutableStateOf(1)


    val timer = object : CountDownTimer(focusTime, 1000) {
        override fun onFinish() {

        }

        override fun onTick(p0: Long) {
            //update label
            currentTimeLeft = p0 / 1000
        }
    }

    fun startTimer() {
        timer.start()
    }
}