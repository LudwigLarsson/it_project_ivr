package com.ludwiglarsson.antiplanner.data.synchronize

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ludwiglarsson.antiplanner.todos.TodoRepository

class MyWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repository: TodoRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val result = repository.getAllTodos()
        return when (result) {
            is TodoRepository.Result.Failure -> Result.retry()
            is TodoRepository.Result.Success -> Result.success()
        }
    }
}
