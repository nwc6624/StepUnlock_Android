package com.stepunlock.app.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.stepunlock.domain.repository.AppRuleRepository
import com.stepunlock.domain.repository.SessionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StepUnlockAccessibilityService : AccessibilityService() {
    
    @Inject
    lateinit var appRuleRepository: AppRuleRepository
    
    @Inject
    lateinit var sessionRepository: SessionRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    companion object {
        private const val TAG = "StepUnlockAccessibility"
        
        var instance: StepUnlockAccessibilityService? = null
            private set
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d(TAG, "Accessibility service connected")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "Accessibility service destroyed")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let { handleAccessibilityEvent(it) }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted")
    }
    
    private fun handleAccessibilityEvent(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString()
                if (packageName != null) {
                    serviceScope.launch {
                        checkIfAppShouldBeLocked(packageName)
                    }
                }
            }
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
        try {
            val intent = Intent(this, LockActivity::class.java).apply {
                putExtra(LockActivity.EXTRA_PACKAGE_NAME, packageName)
                putExtra(LockActivity.EXTRA_APP_NAME, appRule.appName)
                putExtra(LockActivity.EXTRA_UNLOCK_COST, appRule.unlockCostCredits)
                putExtra(LockActivity.EXTRA_UNLOCK_MINUTES, appRule.unlockMinutes)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing lock screen", e)
        }
    }
}
