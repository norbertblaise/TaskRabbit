package com.norbertblaise.taskrabbit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.norbertblaise.taskrabbit.common.AppVisibility
import com.norbertblaise.taskrabbit.common.DataStoreManager
import com.norbertblaise.taskrabbit.common.TimerState
import com.norbertblaise.taskrabbit.common.TimerType
import com.norbertblaise.taskrabbit.util.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "TimerService"

class TimerService : Service() {
    private lateinit var player: MediaPlayer

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var timer: CountDownTimer
    private lateinit var dataStoreManager: DataStoreManager
    private var serviceIsRunning = false
    private val CHANNEL_ID = NotificationUtil.CHANNEL_ID_TIMER
    private val NOTIFICATION_ID = NotificationUtil.TIMER_ID

    //making custom coroutineScope
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    //default timer params
    var focusTime = 5000L
    var shortBreakTime = 2000L
    var longBreakTime = 3000L
    var longBreakInterval = 4
    val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

    //currentTimer values
    private var timeLeftInMillis: Long = -2L
    private lateinit var timerState: TimerState
    private lateinit var timerType: TimerType
    private lateinit var appVisibility: AppVisibility

    private var currentPom: Int = -2

    private val binder = LocalBinder()

    inner class LocalBinder() : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        serviceIsRunning = true
        createNotificationChannel(this, CHANNEL_ID, false)

        dataStoreManager = DataStoreManager(this@TimerService)
        //getting default timer params
        scope.launch {
            dataStoreManager!!.getFromDataStore().collect {
                focusTime = (it.focusTime) * 60 * 1000.toLong()
                shortBreakTime = (it.shortBreak) * 60 * 1000.toLong()
                longBreakTime = (it.longBreak) * 60 * 1000.toLong()
                longBreakInterval = it.longBreakInterval
            }
        }

        //get values from data store
        scope.launch {
            dataStoreManager.getBackgroundTimerParams().catch {
                it.printStackTrace()
            }.collect {
                //get values from datastore
                timeLeftInMillis = it.timeLeftInMillis
                //using the Ints as the ordinal values
                timerState = TimerState.values()[it.timerState]
                timerType = TimerType.values()[it.timerType]
                appVisibility = AppVisibility.values()[it.appVisibility]
                currentPom = it.currentPom

            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = this.baseContext
        //start ringing
        player = MediaPlayer.create(this, defaultRingtoneUri)
        player.setLooping(true)
        player.start()
        startTimer()
        return START_STICKY
        // TODO: attach notification to backgroundtimer
        createNotificationChannel(context, CHANNEL_ID, false)
    }

    private fun createNotificationChannel(
        context: Context,
        channelId: String,
        playSound: Boolean
    ) {
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSound: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationUtil.CHANNEL_ID_TIMER,
                "Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
            notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_hourglass)
                .setAutoCancel(true)
                .setContentText("change this to live data")
                .setOngoing(true)
                .setDefaults(0)
            if (playSound) notificationBuilder.setSound(notificationSound)

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            notificationBuilder.setContentIntent(pendingIntent)
            startForeground(NOTIFICATION_ID, notificationBuilder.build())
        }

    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy: called")
        super.onDestroy()
        serviceIsRunning = false
        GlobalScope.launch {
            dataStoreManager.saveServiceRunningFlag(serviceIsRunning)
        }
        player.stop()
        timer.cancel()

        // save timer data to Datastore
        GlobalScope.launch {
            Log.d(TAG, "onDestroy: Time left in millis is: ${timeLeftInMillis / 1000}")
            dataStoreManager.saveRunningParams(
                timeLeftInMillis,
                timerState = timerState.ordinal,
                timerType = timerType.ordinal,
                appVisibility = appVisibility.ordinal,
                currentPom = currentPom
            )
        }
        job.cancel()
        notificationManager.cancel(NOTIFICATION_ID)
    }

    //////////////////////////////////////////////////////////
    //todo refactor to helper file breaks DNR rule
    private fun startTimer() {
        Timber.tag(TAG).d("startTimer: called with time left " + timeLeftInMillis / 1000)
        // get values from datastore
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                // Update notification text with remaining time
                notificationBuilder.setContentText("Time remaining: ${millisUntilFinished / 1000} seconds")
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
            }

            override fun onFinish() {
                // Timer finished, update notification text
                //todo check next timer and handle accordingly
                notificationBuilder.setContentText("Timer finished")
                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                timerState = TimerState.STOPPED
                if (timerType == TimerType.LONGBREAK) {
                    //stop the timer
                    resetPomodoro()
                } else
                    if (currentPom <= longBreakInterval) {
                        nextTimer()
//                        startTimer()
                    }
                stopSelf()
            }
        }

        timer.start()
    }

    private fun nextTimer() {
        Timber.d("${TAG} nextTimer: Called")
        when (timerType) {
            TimerType.FOCUS -> {

                timerType = if (currentPom % longBreakInterval == 0) {
                    TimerType.LONGBREAK
                } else {
                    TimerType.SHORTBREAK
                }
            }

            TimerType.SHORTBREAK -> {
                currentPom++
                timerType = TimerType.FOCUS
                // TODO: prompt user to continue session
                // TODO: show notification if the app is in background
            }

            TimerType.LONGBREAK -> {
                timerType = TimerType.INITIAL
            }

            else -> {
                //no-op
            }
        }
        Timber.d("${TAG} nextTimer: current timer type is $timerType")
    }

    private fun resetPomodoro() {
        timer.cancel()
        timerType = TimerType.INITIAL
    }
}




