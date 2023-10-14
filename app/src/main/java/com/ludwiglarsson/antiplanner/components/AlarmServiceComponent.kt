package com.ludwiglarsson.antiplanner.components

import com.ludwiglarsson.antiplanner.AlarmServiceScope
import com.ludwiglarsson.antiplanner.data.alarm.DeadlinePostponeService
import dagger.Subcomponent

@AlarmServiceScope
@Subcomponent
interface AlarmServiceComponent {

    fun inject(service: DeadlinePostponeService)

    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmServiceComponent
    }
}