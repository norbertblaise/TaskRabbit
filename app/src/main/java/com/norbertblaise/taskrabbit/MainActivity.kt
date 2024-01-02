package com.norbertblaise.taskrabbit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.models.SettingsModel
import com.norbertblaise.taskrabbit.ui.pomodoro.PomodoroScreen
import com.norbertblaise.taskrabbit.ui.settings.SettingsDetailScreen
import com.norbertblaise.taskrabbit.ui.settings.SettingsScreen
import com.norbertblaise.taskrabbit.ui.settings.SettingsViewModelFactory
import com.norbertblaise.taskrabbit.ui.theme.TaskRabbitTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import timber.log.Timber

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private lateinit var dataStoreManager: DataStoreManager
    private var storeEmpty: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskRabbitApp()
        }
        //init settings
        dataStoreManager = DataStoreManager(this@MainActivity)
        //check if datastore is empty or not
        lifecycleScope.launch {
            dataStoreManager.getFromDataStore().catch {
                it.printStackTrace()
            }.collect {
                storeEmpty = (it.focusTime < 0)
//                if (it.focusTime < 0) {
//                    storeEmpty = true
//                    Log.d(TAG,"onCreate: focusTime is: ${it.focusTime}")
//                }

                Timber.tag(TAG).d("onCreate: storeEmpty is %s", storeEmpty)
                //populate datastore with default data
                if (storeEmpty) {
                    initSettings()
                    storeEmpty = false
                }
            }
        }


        Timber.plant(Timber.DebugTree())
        AndroidLogcatLogger.installOnDebuggableApp(application, minPriority = LogPriority.VERBOSE)

    }

    override fun onResume() {
        super.onResume()
    }

    private fun initSettings() {
        Timber.tag(TAG).d("initSettings: called")
        val defaultSettings = SettingsModel(
            focusTime = 25,
            shortBreak = 5,
            longBreak = 20,
            longBreakInterval = 4
        )
        lifecycleScope.launch {
            dataStoreManager.saveToDataStore(defaultSettings)
        }
    }

    override fun onStop() {
        super.onStop()

    }
}

@Composable
fun TaskRabbitApp() {
    TaskRabbitTheme {
        val dataStoreManager = DataStoreManager(LocalContext.current)
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {

            TaskRabbitNavHost(
                dataStoreManager = dataStoreManager,
                navController = navController,
                modifier = Modifier.padding()
            )
        }
        BackHandler {
            val backStackEntry: NavBackStackEntry? = navController.previousBackStackEntry
            if (backStackEntry != null) {
                val backStackNames = navController
                    .backQueue
                    .map { it.destination.route }
                    .joinToString(", ")

                Log.d("BackStackLog", "Back stack: $backStackNames")
            }
        }
    }
}

@Composable
fun TaskRabbitNavHost(
    dataStoreManager: DataStoreManager,
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
//                viewModel = (PomodoroViewModel(dataStoreManager)),
                onSettingsClick = {
                    Log.d(TAG, "TaskRabbitNavHost: Settings clicked")
                    navController.navigateSingleTopTo(Settings.route)
                }
                //todo add  navigation to ChartScreen
            )
        }
        composable(route = Settings.route) {
            SettingsScreen(
                settingsViewModel = viewModel(factory = SettingsViewModelFactory(dataStoreManager = dataStoreManager)),
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
            SettingsDetailScreen(
                arg = settingsParam!!,
                onUpButtonClicked = {
                    navController.popBackStack()
                }
            )
        }

    }

}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
//        popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
//            saveState = true
//        }
        launchSingleTop = true
    }

private fun NavHostController.navigateToSettingsDetail(settingsCategory: Int) =
    this.navigateSingleTopTo("${SettingsDetail.route}/$settingsCategory")