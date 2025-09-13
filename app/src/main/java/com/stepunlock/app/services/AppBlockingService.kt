package com.stepunlock.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.stepunlock.app.MainActivity
import com.stepunlock.app.R
import com.stepunlock.app.data.entity.AppRuleEntity
import com.stepunlock.app.data.repository.AppRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockingService : Service() {
    
    @Inject
    lateinit var appRepository: AppRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "app_blocking_service"
        private const val CHANNEL_NAME = "App Blocking Service"
        
        fun startService(context: Context) {
            val intent = Intent(context, AppBlockingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, AppBlockingService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startAppMonitoring()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors and blocks apps based on your settings"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("StepUnlock Active")
            .setContentText("Monitoring your app usage")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun startAppMonitoring() {
        serviceScope.launch {
            // TODO: Implement app monitoring logic
            // This would typically involve:
            // 1. Using UsageStatsManager to detect app launches
            // 2. Checking if the launched app is locked
            // 3. If locked, showing the LockActivity
            // 4. If unlocked, allowing access
            
            // For now, we'll just keep the service running
            // The actual monitoring will be implemented in the next step
        }
    }
    
    suspend fun isAppLocked(packageName: String): Boolean {
        val app = appRepository.getAppByPackage(packageName)
        return app?.isLocked == true && !isAppCurrentlyUnlocked(app)
    }
    
    private fun isAppCurrentlyUnlocked(app: AppRuleEntity): Boolean {
        return app.isUnlocked && app.unlockedUntil > System.currentTimeMillis()
    }
    
    suspend fun getLockedApps(): List<AppRuleEntity> {
        return appRepository.getLockedApps().first().filter { it.isLocked }
    }
}
