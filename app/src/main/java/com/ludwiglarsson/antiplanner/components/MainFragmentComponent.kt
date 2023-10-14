package com.ludwiglarsson.antiplanner.components

import com.ludwiglarsson.antiplanner.Callback
import com.ludwiglarsson.antiplanner.MainFragmentScope
import com.ludwiglarsson.antiplanner.fragments.MainFragment
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent
@MainFragmentScope
interface MainFragmentComponent {

    fun inject(fragment: MainFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance callback: Callback): MainFragmentComponent
    }
}