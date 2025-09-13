package com.stepunlock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.stepunlock.app.ui.navigation.StepUnlockNavigation
import com.stepunlock.app.ui.theme.StepUnlockTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Keep splash screen visible while checking for first launch
        splashScreen.setKeepOnScreenCondition { true }
        
        enableEdgeToEdge()
        setContent {
            StepUnlockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StepUnlockNavigation()
                }
            }
        }
        
        // Hide splash screen after a short delay
        splashScreen.setKeepOnScreenCondition { false }
    }
}
