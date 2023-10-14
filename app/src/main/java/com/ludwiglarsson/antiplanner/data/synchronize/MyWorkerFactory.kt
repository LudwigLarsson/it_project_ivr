package com.ludwiglarsson.antiplanner.data.synchronize

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ludwiglarsson.antiplanner.MyWorkerScope
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import javax.inject.Inject
import javax.inject.Provider

@MyWorkerScope
class MyWorkerFactory @Inject constructor(
    private val repository: Provider<TodoRepository>,
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): MyWorker {
        return MyWorker(appContext, workerParameters, repository.get())
    }
}
