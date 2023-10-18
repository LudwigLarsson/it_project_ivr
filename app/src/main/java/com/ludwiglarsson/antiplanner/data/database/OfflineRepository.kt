package com.ludwiglarsson.antiplanner.data.database

import androidx.room.Transaction
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result
import com.ludwiglarsson.antiplanner.RepositoryScope
import com.ludwiglarsson.antiplanner.todos.TodoItem
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@RepositoryScope
class OfflineRepository @Inject constructor(private val todoItemDao: TodoItemDao) : TodoRepository {

    override suspend fun addTodo(item: TodoItem): Result<Unit> {
        return databaseCall {
            val todo = convertToEntity(item)
            todoItemDao.addTodo(todo)
        }
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        return databaseCall {
            todoItemDao.deleteTodo(id)
        }
    }
    override suspend fun deleteAllTodos(): Result<Unit> {
        return databaseCall {
            todoItemDao.deleteAllTodos()
        }
    }

    override suspend fun updateTodo(item: TodoItem): Result<Unit> {
        return databaseCall {
            val todo = convertToEntity(item)
            todoItemDao.updateTodo(todo)
        }
    }

    override suspend fun getTodo(id: String): Result<TodoItem> {
        return databaseCall {
            val result = todoItemDao.getTodo(id)
            return@databaseCall convertFromEntity(result)
        }
    }

    override suspend fun getAllTodos(): Result<List<TodoItem>> {
        return databaseCall {
            val result = todoItemDao.getTodoList()
            return@databaseCall result.map { todoEntity -> convertFromEntity(todoEntity) }
        }
    }

    @Transaction
    override suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>> {
        return databaseCall {
            todoItemDao.deleteAllTodos()
            val newTodoList = updateList.map { todo -> convertToEntity(todo) }
            todoItemDao.updateTodoList(newTodoList)
            val result = todoItemDao.getTodoList()
            return@databaseCall result.map { todoEntity -> convertFromEntity(todoEntity) }
        }
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        val flowListEntity = todoItemDao.getTodoListFlow()
        return flowListEntity.map { list ->
            list.map { entityTodoItem ->
                convertFromEntity(entityTodoItem)
            }
        }.flowOn(Dispatchers.Default)
    }

    private fun convertToEntity(todo: TodoItem): EntityTodoItem {
        return EntityTodoItem(
            todo.itemID,
            todo.itemText,
            todo.itemPriority,
            todo.deadline?.time,
            todo.doneFlag,
            todo.dateOfCreation.time,
            todo.dateOfChanges?.time
        )
    }

    private fun convertFromEntity(todoEntity: EntityTodoItem): TodoItem {
        return TodoItem(
            todoEntity.itemID,
            todoEntity.itemText,
            todoEntity.itemPriority,
            todoEntity.deadline?.let { Date(it) },
            todoEntity.doneFlag,
            Date(todoEntity.dateOfCreation),
            todoEntity.dateOfChanges?.let { Date(it) }
        )
    }

    private suspend fun <R> databaseCall(action: suspend () -> R): Result<R> {
        try {
            val result: R = withContext(Dispatchers.IO) { action() }
            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Failure(e.message.toString())
        }
    }
}