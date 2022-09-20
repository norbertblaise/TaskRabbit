package com.norbertblaise.taskrabbit.models

enum class TimerSettingsParameter(val type: Int) {
    FOCUS_TIME(0),
    SHORT_BREAK(1),
    LONG_BREAK(2),
    LONG_BREAK_INTERVAL(3)
}