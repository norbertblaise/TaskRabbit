package com.norbertblaise.taskrabbit.models

data class SettingsModel (
    var focusTime: Int = 25,
    var shortBreak: Int = 5,
    var LongBreak: Int = 20,
    var LongBreakInterval: Int = 4
)