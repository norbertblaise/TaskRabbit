package com.norbertblaise.taskrabbit.ui.pomodoro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.norbertblaise.taskrabbit.common.TimerType
import org.junit.Test


class PomodoroViewModelTest {

    val viewModel = PomodoroViewModel()

    @Test
    fun setTimerLabel() {
        var timerType = TimerType.INITIAL
        var timerLabel by mutableStateOf("focus")

         viewModel.setTimerLabel()

//        assertEquals(timerLabel)





    }

    @Test
    fun setIndicatorColor() {
    }
}