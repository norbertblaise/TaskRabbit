package com.norbertblaise.taskrabbit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.norbertblaise.taskrabbit.ui.pomodoro.PomodoroScreen
import com.norbertblaise.taskrabbit.ui.settings.SettingsDetailScreen
import com.norbertblaise.taskrabbit.ui.settings.SettingsScreen
import com.norbertblaise.taskrabbit.ui.theme.TaskRabbitTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskRabbitApp()
        }
    }
}

@Composable
fun TaskRabbitApp() {
    TaskRabbitTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            //todo add NavHost
            TaskRabbitNavHost(
                navController = navController, modifier =
                Modifier.padding()
            )
        }
    }
}

@Composable
fun TaskRabbitNavHost(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Pomodoro.route,
        modifier = modifier
    ) {
        composable(route = Pomodoro.route) {
            PomodoroScreen(
                onSettingsClick = {
                    Log.d(TAG, "TaskRabbitNavHost: Settings clicked")
                    navController.navigateSingleTopTo(Settings.route)
                }
                //todo add  navigation to ChartScreen
            )
        }
        composable(route = Settings.route) {
            SettingsScreen(
                onUpButtonClicked = {
                    navController.popBackStack()
                },
                onSettingsItemClicked = { settingsCategory ->

                    navController.navigateToSettingsDetail(settingsCategory)
                },
                onShortBreakClicked = {

                },
                onLongBreakClicked = {

                },
                onLongBreakIntervalClicked = {

                }


            )
        }
        composable(
            route = SettingsDetail.routeWithArgs,
            arguments = SettingsDetail.arguments
        ) { navBackStackEntry ->
            val settingsParam =
                navBackStackEntry.arguments?.getInt(SettingsDetail.settingsParameterType)
            SettingsDetailScreen(arg = settingsParam!!,
                onUpButtonClicked = { navController.popBackStack() })
        }

    }

}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
    }

private fun NavHostController.navigateToSettingsDetail(settingsCategory: Int) =
    this.navigateSingleTopTo("${SettingsDetail.route}/$settingsCategory")