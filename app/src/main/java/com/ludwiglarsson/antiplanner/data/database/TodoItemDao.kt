package com.ludwiglarsson.antiplanner.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM todos WHERE item_id = :id")
    suspend fun getTodo(id: String): EntityTodoItem

    @Query("DELETE FROM todos WHERE item_id = :id")
    suspend fun deleteTodo(id: String)

    @Query("SELECT * FROM todos")
    fun getTodoListFlow(): Flow<List<EntityTodoItem>>

    @Query("SELECT * FROM todos")
    suspend fun getTodoList(): List<EntityTodoItem>

    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()

    @Insert(entity = EntityTodoItem::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todo: EntityTodoItem)

    @Insert(entity = EntityTodoItem::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTodoList(newTodos: List<EntityTodoItem>)

    @Update
    suspend fun updateTodo(todo: EntityTodoItem)
}