package com.ludwiglarsson.antiplanner.data.di

import android.content.Context
import androidx.room.Room
import com.ludwiglarsson.antiplanner.AppScope
import com.ludwiglarsson.antiplanner.data.database.AppDatabase
import com.ludwiglarsson.antiplanner.data.database.TodoItemDao
import dagger.Module
import dagger.Provides

@Module
interface DatabaseModule {

    companion object {
        @Provides
        @AppScope
        fun provideAppDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "todolist_database"
            ).build()
        }

        @Provides
        @AppScope
        fun provideTodoItemDao(appDatabase: AppDatabase): TodoItemDao {
            return appDatabase.todoDao()
        }
    }
}