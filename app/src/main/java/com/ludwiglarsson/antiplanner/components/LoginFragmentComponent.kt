package com.ludwiglarsson.antiplanner.components


import com.ludwiglarsson.antiplanner.LoginFragmentScope
import com.ludwiglarsson.antiplanner.data.di.YandexAuthModule
import com.ludwiglarsson.antiplanner.fragments.LoginFragment
import dagger.Subcomponent


@LoginFragmentScope
@Subcomponent(modules = [YandexAuthModule::class])
interface LoginFragmentComponent {
    fun inject(fragment: LoginFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginFragmentComponent
    }
}