package com.norbertblaise.taskrabbit.models

data class SettingsModel (
    var focusTime: Int = 25,
    var shortBreak: Int = 5,
    var longBreak: Int = 20,
    var longBreakInterval: Int = 4,
)