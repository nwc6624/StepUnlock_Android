package com.stepunlock.app

import android.app.Application
import com.stepunlock.data.local.DataInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class StepUnlockApplication : Application() {
    
    @Inject
    lateinit var dataInitializer: DataInitializer
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize default data
        dataInitializer.initializeData()
    }
}
