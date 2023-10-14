package com.ludwiglarsson.antiplanner.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ludwiglarsson.antiplanner.todos.TodoItem

@Entity(tableName = "todos")
class EntityTodoItem(
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "item_id") val itemID: String,
    @ColumnInfo(name = "item_text") val itemText: String,
    @ColumnInfo(name = "item_priority") val itemPriority: TodoItem.Priority,
    @ColumnInfo(name = "deadline") val deadline: Long?,
    @ColumnInfo(name = "done_flag") val doneFlag: Boolean,
    @ColumnInfo(name = "date_of_creation") val dateOfCreation: Long,
    @ColumnInfo(name = "date_of_changes") val dateOfChanges: Long?,
)