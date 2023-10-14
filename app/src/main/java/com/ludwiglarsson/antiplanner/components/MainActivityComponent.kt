package com.ludwiglarsson.antiplanner.components

import com.ludwiglarsson.antiplanner.MainActivity
import com.ludwiglarsson.antiplanner.MainActivityScope
import dagger.Subcomponent

@Subcomponent
@MainActivityScope
interface MainActivityComponent {

    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }
}