package com.ludwiglarsson.antiplanner

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ludwiglarsson.antiplanner.components.AppComponent
import com.ludwiglarsson.antiplanner.components.DaggerAppComponent
import com.ludwiglarsson.antiplanner.data.synchronize.MyWorker
import com.ludwiglarsson.antiplanner.data.synchronize.MyWorkerFactory
import com.ludwiglarsson.antiplanner.utils.SharedPreferencesHelper
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class App : Application(), Configuration.Provider {

    lateinit var appComponent: AppComponent
        private set

    @Inject
    lateinit var workerFactory: MyWorkerFactory

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
        runPeriodicWorkRequest()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    private fun runPeriodicWorkRequest() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<MyWorker>(DURATION, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(DURATION, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKNAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    companion object {
        private const val WORKNAME = "MyWorker"
        private const val DURATION = 8L
    }
}