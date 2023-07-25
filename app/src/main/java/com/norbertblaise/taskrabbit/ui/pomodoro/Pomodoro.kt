package com.norbertblaise.taskrabbit.ui.pomodoro

data class Pomodoro(
    val workLength: Long,
    val shortBreakDuration: Long,
    val longBreakDuration: Long,
    val longBreakInterval: Int
)