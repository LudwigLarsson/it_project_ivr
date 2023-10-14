package com.ludwiglarsson.antiplanner.data

import android.util.Log
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result.Failure
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result.Success
import com.ludwiglarsson.antiplanner.todos.TodoRepository.Result
import com.ludwiglarsson.antiplanner.RepositoryScope
import com.ludwiglarsson.antiplanner.data.alarm.AlarmRepository
import com.ludwiglarsson.antiplanner.data.di.LocalRevision
import com.ludwiglarsson.antiplanner.data.di.RemoteRevision
import com.ludwiglarsson.antiplanner.data.network.NetworkRepository
import com.ludwiglarsson.antiplanner.data.synchronize.RevisionHolder
import com.ludwiglarsson.antiplanner.todos.TodoItem
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import com.ludwiglarsson.antiplanner.utils.getOr
import com.ludwiglarsson.antiplanner.utils.onFailure
import com.ludwiglarsson.antiplanner.utils.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@RepositoryScope
class CacheRepository @Inject constructor(
    @RemoteRevision private val remoteRevision: RevisionHolder,
    @LocalRevision private val localRevision: RevisionHolder,
    private val remoteRepository: NetworkRepository,
    private val localRepository: AlarmRepository,
) : TodoRepository {

    override suspend fun addTodo(item: TodoItem): Result<Unit> {
        return writeCache { it.addTodo(item) }
    }

    override suspend fun deleteTodo(id: String): Result<Unit> {
        return writeCache { it.deleteTodo(id) }
    }

    override suspend fun updateTodo(item: TodoItem): Result<Unit> {
        return writeCache { it.updateTodo(item) }
    }

    override suspend fun getTodo(id: String): Result<TodoItem> {
        return readCache { it.getTodo(id) }
    }

    override suspend fun getAllTodos(): Result<List<TodoItem>> {
        return readCache { it.getAllTodos() }
    }

    override suspend fun updateAllTodos(updateList: List<TodoItem>): Result<List<TodoItem>> {
        return writeCache { it.updateAllTodos(updateList) }
    }

    override fun observeTodos(): Flow<List<TodoItem>> {
        return localRepository.observeTodos()
    }

    private suspend fun <T> readCache(action: suspend (TodoRepository) -> Result<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            checkCache()
            val remoteResult = action(remoteRepository)
            when (remoteResult) {
                is Success -> remoteResult
                is Failure -> action(localRepository)
            }
        }
    }

    private suspend fun <T> writeCache(action: suspend (TodoRepository) -> Result<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            checkCache()
            val writeRemoteResult = action(remoteRepository)
            when (writeRemoteResult) {
                is Success -> action(localRepository).onSuccess {
                    localRevision.setRevision(remoteRevision.getRevision())
                }
                is Failure -> action(localRepository).onSuccess {
                    localRevision.setRevision(remoteRevision.getRevision() + 1)
                }
            }
        }
    }

    private suspend fun checkCache() {
        val lastKnownRemoteRev = remoteRevision.getRevision()
        val lastRemoteTodos = remoteRepository.getAllTodos().getOr { return }
        val remoteRev = remoteRevision.getRevision()
        val localRev = localRevision.getRevision()
        when {
            localRev > remoteRev -> {
                val items = localRepository.getAllTodos().getOr { return }
                remoteRepository.updateAllTodos(items)
                    .onSuccess {}
                    .onFailure {}
            }
            localRev < remoteRev -> {
                localRepository.updateAllTodos(lastRemoteTodos)
            }
            lastKnownRemoteRev < remoteRev -> {
                localRepository.updateAllTodos(lastRemoteTodos)
            }
            else -> {
            }
        }
        localRevision.setRevision(remoteRevision.getRevision())
    }
}