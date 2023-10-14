package com.ludwiglarsson.antiplanner.data.alarm

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.example.antiplanner.R

object DeadlineNotificationChannel {
    fun NotificationManagerCompat.createDeadlineChannel(context: Context) {
        val channel = NotificationChannelCompat
            .Builder(ALARM_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setVibrationEnabled(true)
            .setName(context.getString(R.string.notification_deadline_channel_name))
            .build()
        createNotificationChannel(channel)
    }

    const val ALARM_CHANNEL_ID = "alarm_channel_id"
}
