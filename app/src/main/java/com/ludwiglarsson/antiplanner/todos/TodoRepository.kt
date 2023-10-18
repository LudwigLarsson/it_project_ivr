package com.ludwiglarsson.antiplanner.todos

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun addTodo(item: TodoItem): Result<Unit>
    suspend fun deleteTodo(id: String): Result<Unit>
    suspend fun updateTodo(item: TodoItem): Result<Unit>
    suspend fun getTodo(id: String): Result<TodoItem>
    suspend fun getAllTodos(): Result<List<TodoItem>>
    suspend fun deleteAllTodos(): Result<Unit>
    suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>>
    fun observeTodos(): Flow<List<TodoItem>>

    sealed interface Result<out T> {
        data class Success<T>(val value: T) : Result<T>
        data class Failure(val message: String) : Result<Nothing>
    }
}