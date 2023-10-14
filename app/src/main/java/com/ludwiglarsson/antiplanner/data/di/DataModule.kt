package com.ludwiglarsson.antiplanner.data.di

import android.content.Context
import android.content.SharedPreferences
import com.ludwiglarsson.antiplanner.AppScope
import com.ludwiglarsson.antiplanner.data.CacheRepository
import com.ludwiglarsson.antiplanner.data.alarm.AlarmDeadlineManager
import com.ludwiglarsson.antiplanner.data.alarm.DeadlineManager
import com.ludwiglarsson.antiplanner.data.synchronize.RevisionHolder
import com.ludwiglarsson.antiplanner.todos.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
interface DataModule {

    @Binds
    fun bindRepository(instance: CacheRepository): TodoRepository

    @Binds
    fun bindDeadlineManager(instance: AlarmDeadlineManager): DeadlineManager

    companion object {
        @Provides
        @AppScope
        fun provideSharedPreference(context: Context): SharedPreferences{
            return context.getSharedPreferences("REVISION", Context.MODE_PRIVATE)
        }

        @Provides
        @AppScope
        @LimitedDispatcher
        fun workerDispatcher(): CoroutineDispatcher {
            return Dispatchers.IO.limitedParallelism(1)
        }

        @AppScope
        @Provides
        @LocalRevision
        fun provideLocalRevision(
            @LimitedDispatcher workDispatcher: CoroutineDispatcher,
            preferences: SharedPreferences,
        ): RevisionHolder {
            return RevisionHolder(LOCAL_KEY, preferences, workDispatcher)
        }

        @AppScope
        @Provides
        @RemoteRevision
        fun provideRemoteRevision(
            @LimitedDispatcher workDispatcher: CoroutineDispatcher,
            preferences: SharedPreferences,
        ): RevisionHolder {
            return RevisionHolder(REMOTE_KEY, preferences, workDispatcher)
        }

        private const val LOCAL_KEY = "RevisionHolder_LocalRevision"
        private const val REMOTE_KEY = "RevisionHolder_RemoteRevision"
    }
}