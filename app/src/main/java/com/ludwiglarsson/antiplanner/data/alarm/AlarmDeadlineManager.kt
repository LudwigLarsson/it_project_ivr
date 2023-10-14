package com.ludwiglarsson.antiplanner.data.alarm

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.createAlarmIntent
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.createEditIntent
import com.ludwiglarsson.antiplanner.data.alarm.AlarmIntentUtils.deleteAlarmIntent
import com.ludwiglarsson.antiplanner.todos.TodoItem
import com.ludwiglarsson.antiplanner.utils.checkPermissions
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class AlarmDeadlineManager @Inject constructor(
    private val context: Context,
) : DeadlineManager {

    private val alarmManager = requireNotNull(context.getSystemService<AlarmManager>())

    override fun setAlarm(todoItem: TodoItem) {
        val deadline = todoItem.deadline
        if (deadline == null || todoItem.doneFlag) {
            cancelAlarm(todoItem.itemID)
            return
        }

        val isGranted = context.checkPermissions(getRequiredPermissions())
        if (!isGranted) {
            return
        }

        val triggerDate = getTriggerDate(deadline)
        if (triggerDate <= Calendar.getInstance().timeInMillis) {
            return
        }

        val alarmIntent = createAlarmIntent(context, todoItem.itemID, todoItem)
        val editIntent = createEditIntent(context, todoItem.itemID)

        try {
            AlarmManagerCompat.setAlarmClock(alarmManager, triggerDate, editIntent, alarmIntent)
        } catch (e: SecurityException) {
        }
    }

    override fun cancelAlarm(itemId: String) {
        val alarmIntent = deleteAlarmIntent(context, itemId)
        if (alarmIntent != null) {
            alarmManager.cancel(alarmIntent)
        }
    }

    private fun getTriggerDate(deadline: Date): Long {
        val triggerCalendar = Calendar.getInstance().apply {
            time = deadline
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return triggerCalendar.timeInMillis
    }

    override fun getRequiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
            )
            else -> emptyList()
        }
    }
}