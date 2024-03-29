package com.norbertblaise.taskrabbit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.norbertblaise.taskrabbit.MainActivity
import com.norbertblaise.taskrabbit.R
import com.norbertblaise.taskrabbit.TimerNotificationActionReceiver
import com.norbertblaise.taskrabbit.common.ACTION_START
import com.norbertblaise.taskrabbit.common.ACTION_STOPPED
import java.text.SimpleDateFormat
import java.util.Date


class NotificationUtil {
    companion object {
        const val CHANNEL_ID_TIMER = "task_rabbit_timer"
        const val CHANNEL_NAME_TIMER = "Task Rabbit Timer"
        const val TIMER_ID = 0

    }

    fun showTimerExpired(context: Context) {
        val startIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        startIntent.action = ACTION_START
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notifBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        notifBuilder.setContentTitle("Timer Complete")
            .setContentText("Continue Session?")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .addAction(R.drawable.ic_play_arrow, "Start", startPendingIntent)

        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
        nManager.notify(TIMER_ID, notifBuilder.build())
    }

    fun showTimerRunning(context: Context, wakeupTime: Long) {
        val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        stopIntent.action = ACTION_STOPPED
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        pauseIntent.action = ACTION_START
        val pausePendingIntent = PendingIntent.getBroadcast(
            context,
            0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

        val notifBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        notifBuilder.setContentTitle("Timer is Running")
            .setContentText("End: ${df.format(Date(wakeupTime))}")
            .setOngoing(true)
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .addAction(R.drawable.ic_play_arrow, "Stop", stopPendingIntent)
            .addAction(R.drawable.ic_play_arrow, "Pause", pausePendingIntent)

        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
        nManager.notify(TIMER_ID, notifBuilder.build())
    }

    fun showTimerPaused(context: Context) {
        val resumeIntent = Intent(context, TimerNotificationActionReceiver::class.java)
        resumeIntent.action = ACTION_START
        val resumePendingIntent = PendingIntent.getBroadcast(
            context,
            0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notifBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)
        notifBuilder.setContentTitle("Timer is Paused")
            .setContentText("Resume?")
            .setContentIntent(getPendingIntentWithStack(context, MainActivity::class.java))
            .addAction(R.drawable.ic_play_arrow, "Resume", resumePendingIntent)

        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
        nManager.notify(TIMER_ID, notifBuilder.build())
    }

    fun hideTimerNotification(context: Context) {
        val nManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nManager.cancel(TIMER_ID)
    }

     fun getBasicNotificationBuilder(
        context: Context,
        channelId: String,
        playSound: Boolean
    ): NotificationCompat.Builder {
        val notificationSound: Uri =
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val nBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_hourglass)
            .setAutoCancel(true)
            .setDefaults(0)
        if (playSound) nBuilder.setSound(notificationSound)
        return nBuilder
    }

    private fun <T> getPendingIntentWithStack(
        context: Context,
        javaClass: Class<T>
    ): PendingIntent {
        val resultIntent = Intent(context, javaClass)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(javaClass)
        stackBuilder.addNextIntent(resultIntent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }

     fun NotificationManager.createNotificationChannel(
        channelId: String,
        channelName: String,
        playSound: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
            else NotificationManager.IMPORTANCE_LOW
            val nChannel = NotificationChannel(channelId, channelName, channelImportance)
            nChannel.enableLights(true)
            nChannel.lightColor = Color.BLUE
            this.createNotificationChannel(nChannel)

        }
    }
}