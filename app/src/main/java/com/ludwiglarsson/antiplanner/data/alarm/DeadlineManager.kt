package com.ludwiglarsson.antiplanner.data.alarm

import com.ludwiglarsson.antiplanner.todos.TodoItem

interface DeadlineManager {
    fun setAlarm(todoItem: TodoItem)
    fun cancelAlarm(itemId: String)
    fun getRequiredPermissions(): List<String>
}