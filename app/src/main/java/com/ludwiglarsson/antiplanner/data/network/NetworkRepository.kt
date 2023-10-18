package com.ludwiglarsson.antiplanner.data.network

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result
import com.ludwiglarsson.antiplanner.RepositoryScope
import com.ludwiglarsson.antiplanner.data.di.RemoteRevision
import com.ludwiglarsson.antiplanner.data.synchronize.RevisionHolder
import com.ludwiglarsson.antiplanner.todos.TodoItem
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import com.ludwiglarsson.antiplanner.utils.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Date
import javax.inject.Inject

@RepositoryScope
class NetworkRepository @Inject constructor(
    @RemoteRevision private val revision: RevisionHolder,
    private val service: RetrofitService,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    context: Context
) : TodoRepository {

    private val androidID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    override suspend fun addTodo(item: TodoItem): Result<Unit> {
        return networkCall(action = {
            val result = service.addTodo(
                sharedPreferencesHelper.getTokenForResponse(), revision.getRevision(), TodoItemRequest(converterRequest(item)))
            revision.setRevision(result.revision)
        })
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        return networkCall(action = {
            val result = service.deleteTodo(sharedPreferencesHelper.getTokenForResponse(), revision.getRevision(), id)
            revision.setRevision(result.revision)
        })
    }
    override suspend fun deleteAllTodos(): Result<Unit> {
        return networkCall(action = {
            val result = service.deleteAllTodos(sharedPreferencesHelper.getTokenForResponse(), revision.getRevision())
            revision.setRevision(result.revision)
        })
    }

    override suspend fun updateTodo(item: TodoItem): Result<Unit> {
        return networkCall(action = {
            val result = service.updateTodo(sharedPreferencesHelper.getTokenForResponse(),
                revision.getRevision(),
                item.itemID,
                TodoItemRequest(converterRequest(item))
            )
            revision.setRevision(result.revision)
        })
    }

    override suspend fun getTodo(id: String): Result<TodoItem> {
        return networkCall(action = {
            val response = service.getTodo(id)
            val todo = converterResponse(response.element)
            revision.setRevision(response.revision)
            return@networkCall todo
        })
    }

    override suspend fun getAllTodos(): Result<List<TodoItem>> {
        return networkCall(action = {
            val response = service.getTodoList(sharedPreferencesHelper.getTokenForResponse())
            val listTodoItem = response.list.map { pojo: TodoItemPOJO ->
                converterResponse(pojo)
            }
            revision.setRevision(response.revision)
            return@networkCall listTodoItem
        })
    }

    override suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>> {
        return networkCall {
            val listUpdateTodoItem = updateList.map { todo -> converterRequest(todo) }
            val response = service.updateTodosOnServer(sharedPreferencesHelper.getTokenForResponse(), revision.getRevision(), TodoItemListRequest(listUpdateTodoItem))
            val listTodoItem = response.list.map { pojo: TodoItemPOJO ->
                converterResponse(pojo)
            }
            revision.setRevision(response.revision)
            return@networkCall listTodoItem
        }
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        throw NotImplementedError("Нет сокетов.")
    }

    private fun converterResponse(pojo: TodoItemPOJO): TodoItem {
        val priority = when (pojo.itemPriority) {
            TodoItemPOJO.Priority.LOW -> TodoItem.Priority.LOW
            TodoItemPOJO.Priority.NORMAL -> TodoItem.Priority.NORMAL
            TodoItemPOJO.Priority.HIGH -> TodoItem.Priority.HIGH
        }

        return TodoItem(
            pojo.itemID,
            pojo.itemText,
            priority,
            pojo.deadline?.let { Date(it) },
            pojo.doneFlag,
            Date(pojo.dateOfCreation),
            pojo.dateOfChanges?.let { Date(it) }
        )
    }

    private fun converterRequest(todoItem: TodoItem): TodoItemPOJO {
        val priority = when (todoItem.itemPriority) {
            TodoItem.Priority.LOW -> TodoItemPOJO.Priority.LOW
            TodoItem.Priority.NORMAL -> TodoItemPOJO.Priority.NORMAL
            TodoItem.Priority.HIGH -> TodoItemPOJO.Priority.HIGH
        }

        return TodoItemPOJO(
            todoItem.itemID,
            todoItem.itemText,
            priority,
            todoItem.deadline?.time,
            todoItem.doneFlag,
            todoItem.dateOfCreation.time,
            todoItem.dateOfChanges?.time,
            androidID
        )
    }

    private suspend fun <R> networkCall(action: suspend () -> R): Result<R> {
        try {
            val result: R = withContext(Dispatchers.IO) { withRetry(action) }
            return Result.Success(result)
        } catch (e: HttpException) {
            return Result.Failure(e.message())
        } catch (e: IOException) {
            return Result.Failure(e.message.toString())
        }
    }

    private suspend fun <R> withRetry(action: suspend () -> R): R {
        var attempt = 0
        lateinit var exception: Exception
        while (attempt != MAX_RETRY_ATTEMPTS) {
            try {
                delay(attempt * RETRY_DELAY_MS)
                return action()
            } catch (e: HttpException) {
                if (!RETRY_IS_ALLOWED.contains(e.code())) {
                    throw e
                } else {
                    exception = e
                }
            } catch (e: IOException) {
                exception = e
            } finally {
                attempt++
            }
        }
        throw exception
    }

    companion object {
        private const val RETRY_DELAY_MS = 300L
        private const val MAX_RETRY_ATTEMPTS = 3
        private val RETRY_IS_ALLOWED = setOf(
            404, 408, 419, 425, 449, 499, 503, 504, 509, 520, 521, 522, 523, 524, 525, 526
        )
    }
}