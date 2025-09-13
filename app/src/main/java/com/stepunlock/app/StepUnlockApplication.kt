package com.stepunlock.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StepUnlockApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Database and dependencies are initialized by Hilt
        // TODO: Initialize default data
    }
}
