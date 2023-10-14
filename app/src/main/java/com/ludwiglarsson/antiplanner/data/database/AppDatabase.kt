package com.ludwiglarsson.antiplanner.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EntityTodoItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoItemDao
}