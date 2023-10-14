package com.ludwiglarsson.antiplanner.todos

import java.util.Date

data class TodoItem(
    val itemID: String,
    val itemText: String,
    val itemPriority: Priority,
    val deadline: Date?,
    val doneFlag: Boolean,
    val dateOfCreation: Date,
    val dateOfChanges: Date?
) {
    enum class Priority {
        LOW,
        NORMAL,
        HIGH
    }
}