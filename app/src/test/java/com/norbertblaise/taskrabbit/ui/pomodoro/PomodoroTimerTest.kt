package com.norbertblaise.taskrabbit.ui.pomodoro

import com.norbertblaise.taskrabbit.common.TimerState
import org.junit.Test

class PomodoroTimerTest {

    @Test
    fun startShortBreak() {
        var timerState = TimerState.STOPPED

       val pomodoroTimer = PomodoroTimer()

        pomodoroTimer.startShortBreak()
    }
}