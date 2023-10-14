package com.ludwiglarsson.antiplanner.components

import android.content.Context
import com.ludwiglarsson.antiplanner.App
import com.ludwiglarsson.antiplanner.AppScope
import com.ludwiglarsson.antiplanner.MyWorkerScope
import com.ludwiglarsson.antiplanner.RepositoryScope
import com.ludwiglarsson.antiplanner.data.di.DataModule
import com.ludwiglarsson.antiplanner.data.di.DatabaseModule
import com.ludwiglarsson.antiplanner.data.di.NetworkModule
import com.ludwiglarsson.antiplanner.viewmodels.ViewModelModule
import com.ludwiglarsson.antiplanner.viewmodels.ViewModelsMap
import dagger.BindsInstance
import dagger.Component

@AppScope
@MyWorkerScope
@RepositoryScope
@Component(modules = [DataModule::class, DatabaseModule::class, NetworkModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(app: App)

    fun getViewModelsMap(): ViewModelsMap

    fun getMainFragmentComponentFactory(): MainFragmentComponent.Factory

    fun getMainActivityComponentFactory(): MainActivityComponent.Factory
    fun getAlarmServiceComponentFactory(): AlarmServiceComponent.Factory
    fun getEditFragmentComponentFactory(): EditFragmentComponent.Factory

    fun getNewFragmentComponentFactory(): NewFragmentComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}