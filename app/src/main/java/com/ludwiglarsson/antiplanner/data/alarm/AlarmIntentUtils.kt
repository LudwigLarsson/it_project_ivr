package com.ludwiglarsson.antiplanner.data.alarm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.PendingIntentCompat
import com.ludwiglarsson.antiplanner.MainActivity
import com.ludwiglarsson.antiplanner.todos.TodoItem

object AlarmIntentUtils {

    private const val ID_KEY = "IntentUtils_id"
    private const val NAME_KEY = "IntentUtils_name"
    private const val PRIORITY_KEY = "IntentUtils_priority"

    fun Intent.getItemId(): String? {
        return getStringExtra(ID_KEY)
    }

    fun Intent.getItemName(): String? {
        return getStringExtra(NAME_KEY)
    }

    fun Intent.getItemPriority(): TodoItem.Priority? {
        val enumOrdinal = getIntExtra(PRIORITY_KEY, -1)
        return TodoItem.Priority.values().find { it.ordinal == enumOrdinal }
    }

    fun createAlarmIntent(context: Context, itemId: String, item: TodoItem): PendingIntent {
        val serviceIntent = Intent(context, DeadlineNotificationService::class.java)
        serviceIntent.putItemId(itemId)
        serviceIntent.putItemInformation(item)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntentCompat.getForegroundService(
                context,
                itemId.hashCode(),
                serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        } else {
            PendingIntentCompat.getService(
                context,
                itemId.hashCode(),
                serviceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        }
    }

    fun deleteAlarmIntent(context: Context, itemId: String): PendingIntent? {
        val serviceIntent = Intent(context, DeadlineNotificationService::class.java)
        serviceIntent.putItemId(itemId)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                context,
                itemId.hashCode(),
                serviceIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
        } else {
            PendingIntent.getService(
                context,
                itemId.hashCode(),
                serviceIntent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }

    fun createEditIntent(context: Context, itemId: String): PendingIntent {
        val activityIntent = Intent(context, MainActivity::class.java)
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activityIntent.putItemId(itemId)
        return PendingIntentCompat.getActivity(
            context,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT,
            false
        )
    }

    fun createPostponeIntent(context: Context, itemId: String): PendingIntent {
        val postponeIntent = DeadlinePostponeService.createPostponeIntent(context)
        postponeIntent.putItemId(itemId)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntentCompat.getForegroundService(
                context,
                itemId.hashCode(),
                postponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        } else {
            PendingIntentCompat.getService(
                context,
                itemId.hashCode(),
                postponeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT,
                false
            )
        }
    }

    private fun Intent.putItemId(itemId: String): Intent {
        return putExtra(ID_KEY, itemId)
    }

    private fun Intent.putItemInformation(item: TodoItem): Intent {
        putExtra(PRIORITY_KEY, item.itemPriority.ordinal)
        putExtra(NAME_KEY, item.itemText)
        return this
    }
}