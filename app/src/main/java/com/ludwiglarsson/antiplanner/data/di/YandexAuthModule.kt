package com.ludwiglarsson.antiplanner.data.di

import android.content.Context
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import dagger.Module
import dagger.Provides

@Module
class YandexAuthModule {
    @Provides
    fun provideYandexAuthSdk(context: Context): YandexAuthSdk {
        return YandexAuthSdk(context, YandexAuthOptions(context, true))
    }
}