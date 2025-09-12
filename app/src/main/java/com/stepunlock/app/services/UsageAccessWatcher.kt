package com.stepunlock.app.services

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.stepunlock.domain.repository.AppRuleRepository
import com.stepunlock.domain.repository.SessionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsageAccessWatcher : Service() {
    
    @Inject
    lateinit var appRuleRepository: AppRuleRepository
    
    @Inject
    lateinit var sessionRepository: SessionRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 1000L // Check every second
    private var isRunning = false
    
    companion object {
        private const val TAG = "UsageAccessWatcher"
        
        fun startService(context: Context) {
            val intent = Intent(context, UsageAccessWatcher::class.java)
            context.startService(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, UsageAccessWatcher::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            startWatching()
        }
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopWatching()
    }
    
    private fun startWatching() {
        isRunning = true
        Log.d(TAG, "Starting usage access watcher")
        
        val runnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    checkCurrentApp()
                    handler.postDelayed(this, checkInterval)
                }
            }
        }
        handler.post(runnable)
    }
    
    private fun stopWatching() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        Log.d(TAG, "Stopped usage access watcher")
    }
    
    private fun checkCurrentApp() {
        serviceScope.launch {
            try {
                val currentPackage = getCurrentAppPackage()
                if (currentPackage != null) {
                    checkIfAppShouldBeLocked(currentPackage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking current app", e)
            }
        }
    }
    
    private fun getCurrentAppPackage(): String? {
        return try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val time = System.currentTimeMillis()
            val events = usageStatsManager.queryEvents(time - 1000, time)
            
            var currentPackage: String? = null
            val event = UsageEvents.Event()
            
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                    event.eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    currentPackage = event.packageName
                }
            }
            
            currentPackage
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current app package", e)
            null
        }
    }
    
    private suspend fun checkIfAppShouldBeLocked(packageName: String) {
        try {
            // Check if app is locked
            val appRuleResult = appRuleRepository.getAppRule(packageName)
            if (appRuleResult.isSuccess) {
                val appRule = appRuleResult.getOrNull()
                if (appRule?.locked == true) {
                    // Check if there's an active session for this app
                    val sessionResult = sessionRepository.getActiveSessionForApp(packageName)
                    if (sessionResult.isSuccess) {
                        val activeSession = sessionResult.getOrNull()
                        if (activeSession == null || activeSession.isExpired) {
                            // App should be locked, show lock screen
                            showLockScreen(packageName, appRule)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if app should be locked", e)
        }
    }
    
    private fun showLockScreen(packageName: String, appRule: com.stepunlock.domain.model.AppRule) {
        // TODO: Launch LockActivity
        Log.d(TAG, "Should show lock screen for $packageName")
    }
}
