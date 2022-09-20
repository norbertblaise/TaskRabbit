package com.norbertblaise.taskrabbit

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Contract for information needed on every TaskRabbit navigation destination
 */
interface TaskRabbitDestination {
    val route: String
}

/**
 *Task Rabbit app destinations
 */

object Pomodoro : TaskRabbitDestination {
    override val route: String
        get() = "pomodoro"

}

object Settings : TaskRabbitDestination {
    override val route: String
        get() = "settings"

}

object SettingsDetail : TaskRabbitDestination {
    override val route: String
        get() = "settings_detail"

    const val settingsParameterType = "settings_parameterType"
    val routeWithArgs = "${route}/{${settingsParameterType}}"
    val arguments = listOf(
        navArgument(settingsParameterType) { type = NavType.IntArrayType }
    )
}
