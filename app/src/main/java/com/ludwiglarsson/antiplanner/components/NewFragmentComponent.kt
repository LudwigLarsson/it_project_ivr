package com.ludwiglarsson.antiplanner.components

import com.ludwiglarsson.antiplanner.NewFragmentScope
import dagger.Subcomponent

@Subcomponent
@NewFragmentScope
interface NewFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): NewFragmentComponent
    }
}
