package com.norbertblaise.taskrabbit.ui.pomodoro

import android.os.CountDownTimer

class PomodoroTimer {
    // The length of a pomodoro in milliseconds
    private val pomodoroLength = 25 * 60 * 1000

    // The length of a long break in milliseconds
    private val longBreakLength = 15 * 60 * 1000

    // The length of a short break in milliseconds
    private val shortBreakLength = 5 * 60 * 1000

    // The number of pomodoros before a long break
    private val pomodorosBeforeLongBreak = 4

    // The current pomodoro count
    private var pomodoroCount = 0

    // The current state of the timer
    private var timerState = TimerState.POMODORO

    // The CountDownTimer object for the pomodoro timer
    private lateinit var timer: CountDownTimer

    // An enum for the different states of the timer
    private enum class TimerState {
        POMODORO,
        SHORT_BREAK,
        LONG_BREAK,
        STOPPED
    }

    // A listener interface for timer updates
    interface OnTimerUpdateListener {
        fun onTick(millisUntilFinished: Long)
        fun onFinish()
    }

    // The OnTimerUpdateListener for the timer
    private var listener: OnTimerUpdateListener? = null

    /**
     * Sets the OnTimerUpdateListener for the timer
     */
    fun setOnTimerUpdateListener(listener: OnTimerUpdateListener) {
        this.listener = listener
    }

    /**
     * Starts the timer
     */
    fun startTimer() {
        when (timerState) {
            TimerState.POMODORO -> startPomodoro()
            TimerState.SHORT_BREAK -> startShortBreak()
            TimerState.LONG_BREAK -> startLongBreak()
            TimerState.STOPPED -> {
                // If the timer was previously stopped, reset the pomodoro count and start a new pomodoro
                pomodoroCount = 0
                startPomodoro()
            }
        }
    }

    /**
     * Stops the timer
     */
    fun stopTimer() {
        timer.cancel()
        timerState = TimerState.STOPPED
    }

    /**
     * Starts a pomodoro
     */
    private fun startPomodoro() {
        timerState = TimerState.POMODORO
        timer = object : CountDownTimer(pomodoroLength.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                listener?.onTick(millisUntilFinished)
            }

            override fun onFinish() {
                pomodoroCount++
                if (pomodoroCount % pomodorosBeforeLongBreak == 0) {
                    startLongBreak()
                } else {
                    startShortBreak()
                }
            }
        }
        timer.start()
    }

    private fun startLongBreak() {
        TODO("Not yet implemented")
    }

    /**
     * Starts a short break
     */
     fun startShortBreak() {
        timerState = TimerState.SHORT_BREAK
    }
}