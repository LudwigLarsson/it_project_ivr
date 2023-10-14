package com.ludwiglarsson.antiplanner.data.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.antiplanner.R
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result
import com.ludwiglarsson.antiplanner.utils.getOr
import com.ludwiglarsson.antiplanner.App
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.getItemId
import com.ludwiglarsson.antiplanner.data.alarm.DeadlineNotificationChannel.ALARM_CHANNEL_ID
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class DeadlinePostponeService : Service() {

    @Inject
    lateinit var repository: TodoRepository

    private val scope = CoroutineScope(Dispatchers.IO)
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onCreate() {
        (application as App).appComponent.getAlarmServiceComponentFactory().create().inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handlePostponeIntent(intent, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun handlePostponeIntent(intent: Intent?, startId: Int) {
        if (intent == null) {
            stopSelfResult(startId)
            return
        }

        val itemId = intent.getItemId()
        val action = intent.action
        if (itemId == null || action != POSTPONE_ACTION) {
            stopSelfResult(startId)
            return
        }

        setUpForegroundNotification(itemId)
        scope.launch {
            val result = postponeAlarm(itemId)
            when (result) {
                is Result.Success -> {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                }

                is Result.Failure -> {
                    setUpErrorNotification(itemId)
                    stopForeground(STOP_FOREGROUND_DETACH)
                }
            }
            stopSelfResult(startId)
        }
    }

    private suspend fun postponeAlarm(itemId: String): Result<Unit> {
        val todoItem = repository.getTodo(itemId).getOr { return it }
        val deadline = todoItem.deadline
        if (deadline == null) {
            return Result.Success(Unit)
        }
        val postponedDeadline = deadline.time + POSTPONE_TIME_MILLIS
        val postponedTodoItem = todoItem.copy(deadline = Date(postponedDeadline))
        return repository.updateTodo(postponedTodoItem)
    }

    private fun setUpForegroundNotification(itemId: String) {
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.postpone)
            .setContentTitle(getString(R.string.notification_deadline_postpone_title))
            .setContentText(getString(R.string.notification_deadline_postpone_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        try {
            startForeground(itemId.hashCode(), notification)
        } catch (e: SecurityException) {
            return
        }
    }

    private fun setUpErrorNotification(itemId: String) {
        val retryIntent = AlarmIntentUtils.createPostponeIntent(this, itemId)
        val notification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.error_filled)
            .setContentTitle(getString(R.string.notification_deadline_postpone_error_title))
            .setContentText(getString(R.string.notification_deadline_postpone_error_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(retryIntent)
            .build()

        try {
            notificationManager.notify(itemId.hashCode(), notification)
        } catch (e: SecurityException) {
            return
        }
    }

    companion object {
        private const val POSTPONE_ACTION =
            "com.example.todoapp.data.alarm.DeadlinePostponeService_postpone"
        private const val POSTPONE_TIME_MILLIS = 24 * 60 * 60 * 1000

        fun createPostponeIntent(context: Context): Intent {
            return Intent(context, DeadlinePostponeService::class.java).apply {
                action = POSTPONE_ACTION
            }
        }
    }
}