package com.ludwiglarsson.antiplanner.data.di

import com.google.gson.GsonBuilder
import com.ludwiglarsson.antiplanner.AppScope
import com.ludwiglarsson.antiplanner.data.network.AuthInterceptor
import com.ludwiglarsson.antiplanner.data.network.NetworkRepository
import com.ludwiglarsson.antiplanner.data.network.RetrofitService
import com.ludwiglarsson.antiplanner.utils.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
interface NetworkModule {

    companion object {
        @Provides
        fun provideAuthInterceptor(): AuthInterceptor {
            return AuthInterceptor()
        }

        @Provides
        @AppScope
        fun provideOkhttp(authInterceptor: AuthInterceptor): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            return OkHttpClient
                .Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        }

        @Provides
        @AppScope
        fun provideRetrofit(okHttpClient: OkHttpClient): RetrofitService {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://beta.mrdekk.ru/todobackend/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
            return retrofit.create(RetrofitService::class.java)
        }
        /*@Provides
        @Reusable
        fun provideNetworkRepository(
            sharedPreferencesHelper: SharedPreferencesHelper,
            retrofitService: RetrofitService
        ): NetworkRepository = NetworkRepository(sharedPreferencesHelper, retrofitService)*/
    }
}