package com.norbertblaise.taskrabbit

/**
 * Contract for information needed on every TaskRabbit navigation destination
 */
interface TaskRabbitDestination {
    val route: String
}

/**
 *Task Rabbit app destinations
 */

object Pomodoro : TaskRabbitDestination{
    override val route: String
        get() = "pomodoro"

}

object Settings : TaskRabbitDestination{
    override val route: String
        get() = "settings"

}