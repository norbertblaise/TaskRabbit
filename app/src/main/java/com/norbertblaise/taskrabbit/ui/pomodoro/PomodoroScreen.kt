package com.norbertblaise.taskrabbit.ui.pomodoro

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType
import com.norbertblaise.taskrabbit.repository.SettingsRepositoryImpl
import com.norbertblaise.taskrabbit.ui.components.TimerProgressIndicator
import com.norbertblaise.taskrabbit.ui.theme.Grey
import com.norbertblaise.taskrabbit.ui.theme.Ink
import com.norbertblaise.taskrabbit.ui.theme.Salmon500
import kotlinx.coroutines.launch
import timber.log.Timber


private const val TAG = "PomodoroScreen"

fun showToast(context: Context) {
    Toast.makeText(context, "Only breaks can be skipped", Toast.LENGTH_LONG).show()
    Timber.d("$TAG showToast: called")
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PomodoroScreen(
//    viewModel: PomodoroViewModel = viewModel(),
    onSettingsClick: () -> Unit = {},
    onChartClick: () -> Unit = {},
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
//    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context = LocalContext.current)
    val settingsRepositoryImpl = SettingsRepositoryImpl(context = LocalContext.current)

    val viewModel: PomodoroViewModel =
        viewModel(
            factory = PomodoroViewModelFactory(
                dataStoreManager,
                settingsRepositoryImpl,
                lifecycleOwner
            )
        )

    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        // for lifecycle events
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_CREATE",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Lifecycle.Event.ON_START -> {

                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_START",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Lifecycle.Event.ON_RESUME -> {
//                    currentOnResume()
                    lifecycleOwner.lifecycleScope.launch {
                        viewModel.onActivityStarted(context)
                    }
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_RESUME",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Lifecycle.Event.ON_PAUSE -> {

                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_PAUSE",
                        Toast.LENGTH_SHORT
                    ).show()
//                    currentOnPause()
                }

                Lifecycle.Event.ON_STOP -> {


                    lifecycleOwner.lifecycleScope.launch {
                        if (viewModel.timerState == TimerState.RUNNING) {
                            viewModel.onActivityStopped(context)
                        }
                    }
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_STOP",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    Toast.makeText(
                        context,
                        "DisposableEffectWithLifeCycle ON_DESTROY",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {}
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

            Toast.makeText(
                context,
                "DisposableEffectWithLifeCycle composition EXIT",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    Scaffold(
        modifier = Modifier.padding(16.dp),
        topBar = {
            PomodoroScreenAppBar(
                onSettingsClicked = onSettingsClick,
                onChartClicked = onChartClick
            )
        },
        content = {
            PomodoroScreenBody(
                viewModel = viewModel
            )
        }
    )
}

@Composable
fun PomodoroScreenAppBar(
    onSettingsClicked: () -> Unit = {},
    onChartClicked: () -> Unit = {}
) {
    TopAppBar(
        title = { Text("") },
        backgroundColor = Color.White,
        elevation = 0.dp,
        actions = {
            IconButton(onClick = onChartClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chart_outlined),
                    tint = Ink,
                    contentDescription = "View Stats button" // todo extract string resource
                )
            }
            IconButton(onClick = onSettingsClicked) {
                Icon(
                    Icons.Default.Settings,
                    tint = Ink,
                    contentDescription = "Open Settings" //todo convert to string resours
                )
            }
        }
    )
}

@Composable
fun PomodoroScreenBody(viewModel: PomodoroViewModel) {
    val mContext = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ShowAlertDialog(
            show = viewModel.showDialog,
            onDismiss = viewModel::onDialogDismiss,
            onConfirm = viewModel::onDialogConfirm
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Task Rabbit",
            style = MaterialTheme.typography.h2
        )
        Spacer(modifier = Modifier.height(80.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
//            Timber.d("Pomodoro Screen, Time left in percent is $timeLeftInPercent")
//            Timber.d("Pomodoro Screen, Time left in percent  from viewmodel is ${viewModel.currentTimeLeftInPercentage.toFloat()}")

            TimerProgressIndicator(
                percentage = viewModel.currentTimeLeftInPercentage.toFloat(),
                initialTimerValue = viewModel.timerDuration,
                timeLeft = viewModel.currentTimeLeftInMillis,
                label = viewModel.timerLabel,
                currentPom = viewModel.currentPom.toString(),
                numPoms = viewModel.longBreakInterval.toString(),
                color = viewModel.indicatorColour
            )
        }
        Spacer(modifier = Modifier.height(70.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            FloatingActionButton(
                onClick = { viewModel.onResetButtonClick() },
                modifier = Modifier
                    .size(40.dp),
                backgroundColor = Grey,
                contentColor = Ink

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_replay),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            ExtendedFloatingActionButton(
                onClick = {
                    Timber.d("PomodoroScreenBody: startbutton clicked")
                    viewModel.onStartStopButtonClick()
                },
                icon = {
                    Icon(
                        painter = painterResource(id = viewModel.startPauseButtonIcon),
                        contentDescription = null
                    )
                },
                text = { Text(text = viewModel.startPauseButtonText) },
                modifier = Modifier,
                backgroundColor = Salmon500,
                contentColor = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (viewModel.timerType == TimerType.FOCUS) {
                        showToast(mContext)
                    } else if (viewModel.timerType == TimerType.SHORTBREAK || viewModel.timerType == TimerType.LONGBREAK) {
                        viewModel.showDialog = true
                    }
                },
                modifier = Modifier
                    .size(40.dp),
                backgroundColor = Grey,
                contentColor = Ink
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_skip_next),
                    contentDescription = null
                )
            }

        }
    }
}

@Composable
fun ShowAlertDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text("Skip Rest")

            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Salmon500,
                        backgroundColor = Color.Transparent
                    )

                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Salmon500,
                        contentColor = Color.White
                    )
                ) {
                    Text("No")
                }
            },

            text = {
                Text("Are you sure you want to skip rest?")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PomodoroScreenTopAppBarPreview() {
    PomodoroScreenAppBar()
}

@Preview(showBackground = true)
@Composable
fun PomodoroScreenBodyPreview() {
    PomodoroScreenBody(viewModel = viewModel())
}

@Preview(showBackground = true)
@Composable
fun PomodoroScreenPreview() {
//    PomodoroScreen()
}