package com.norbertblaise.taskrabbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.norbertblaise.taskrabbit.ui.pomodoro.PomodoroScreen
import com.norbertblaise.taskrabbit.ui.settings.SettingsScreen
import com.norbertblaise.taskrabbit.ui.theme.TaskRabbitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskRabbitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    //todo add NavHost
                }
            }
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
                onSettingsClick = { navController.navigateSingleTopTo(Settings.route) }
            //todo add  navigation to ChartScreen
            )
        }
        composable(route = Settings.route) {
            SettingsScreen()
        }
    }

}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
            saveState = true
        }
    }