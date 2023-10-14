package com.ludwiglarsson.antiplanner.data.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.antiplanner.R
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.createEditIntent
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.createPostponeIntent
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.getItemId
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.getItemName
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.getItemPriority
import com.ludwiglarsson.antiplanner.data.alarm.DeadlineNotificationChannel.ALARM_CHANNEL_ID
import com.ludwiglarsson.antiplanner.data.alarm.DeadlineNotificationChannel.createDeadlineChannel
import com.ludwiglarsson.antiplanner.todos.TodoItem

class DeadlineNotificationService : Service() {

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onCreate() {
        notificationManager.createDeadlineChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleAlarmIntent(intent)
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handleAlarmIntent(intent: Intent?) {
        val name = intent?.getItemName()
        val itemId = intent?.getItemId()
        val priority = intent?.getItemPriority()
        if (itemId != null && priority != null && name != null) {
            setUpNotification(itemId, name, priority)
        } else {
        }
    }

    private fun setUpNotification(itemId: String, name: String, priority: TodoItem.Priority) {
        val activityIntent = createEditIntent(this, itemId)
        val postponeIntent = createPostponeIntent(this, itemId)
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(
                when (priority) {
                    TodoItem.Priority.LOW -> R.drawable.low_priority
                    TodoItem.Priority.NORMAL -> R.drawable.empty
                    TodoItem.Priority.HIGH -> R.drawable.high_priority
                }
            )
            .setContentTitle(name)
            .setContentText(
                when (priority) {
                    TodoItem.Priority.LOW -> this.getString(R.string.notification_low_priority)
                    TodoItem.Priority.NORMAL -> this.getString(R.string.notification_normal_priority)
                    TodoItem.Priority.HIGH -> this.getString(R.string.notification_high_priority)
                }
            )
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(activityIntent)
            .addAction(
                R.drawable.postpone,
                getString(R.string.notification_deadline_postpone_button),
                postponeIntent
            )
            .build()

        try {
            startForeground(itemId.hashCode(), notification)
            stopForeground(STOP_FOREGROUND_DETACH)
        } catch (e: SecurityException) {
            return
        }
    }
}