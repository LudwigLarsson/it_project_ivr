package com.ludwiglarsson.antiplanner.viewmodels

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ludwiglarsson.antiplanner.App
import dagger.MapKey
import javax.inject.Provider
import kotlin.reflect.KClass

typealias ViewModelsMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@MainThread
@Suppress("UNCHECKED_CAST")
inline fun <reified VM : ViewModel> Fragment.appViewModels(): Lazy<VM> {
    return viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val viewModelMap = (requireContext().applicationContext as App).appComponent.getViewModelsMap()
                val viewModelProvider = viewModelMap.getValue(VM::class.java)
                return viewModelProvider.get() as T
            }
        }
    }
}
