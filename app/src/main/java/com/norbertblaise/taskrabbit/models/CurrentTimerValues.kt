package com.norbertblaise.taskrabbit.models

data class CurrentTimerValues(
    val timeLeftInMillis: Long,
    val timerType: Int,
    val timerState: Int,
    val appVisibility: Int,
    val currentPom: Int,

)