package com.ludwiglarsson.antiplanner.components


import com.ludwiglarsson.antiplanner.EditFragmentScope
import dagger.Subcomponent

@Subcomponent
@EditFragmentScope
interface EditFragmentComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EditFragmentComponent
    }
}