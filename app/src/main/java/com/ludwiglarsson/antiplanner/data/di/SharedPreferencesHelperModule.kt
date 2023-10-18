package com.ludwiglarsson.antiplanner.data.di

import android.content.Context
import com.ludwiglarsson.antiplanner.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class SharedPreferencesHelperModule {

    @Provides
    @Reusable
    fun provideSharedPreferences(context: Context): SharedPreferencesHelper =
        SharedPreferencesHelper(context)
}