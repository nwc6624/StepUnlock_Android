package com.stepunlock.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.stepunlock.app.MainActivity
import com.stepunlock.app.R
import com.stepunlock.domain.repository.SessionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UnlockTimerService : Service() {
    
    @Inject
    lateinit var sessionRepository: SessionRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 30000L // Check every 30 seconds
    private var isRunning = false
    
    companion object {
        private const val TAG = "UnlockTimerService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "unlock_timer_channel"
        
        fun startService(context: Context) {
            val intent = Intent(context, UnlockTimerService::class.java)
            context.startForegroundService(intent)
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, UnlockTimerService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            startForeground(NOTIFICATION_ID, createNotification())
            startTimer()
        }
        return START_STICKY
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Unlock Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows active unlock sessions"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("StepUnlock Active")
            .setContentText("Monitoring unlock sessions")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun startTimer() {
        isRunning = true
        Log.d(TAG, "Starting unlock timer service")
        
        val runnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    checkExpiredSessions()
                    handler.postDelayed(this, checkInterval)
                }
            }
        }
        handler.post(runnable)
    }
    
    private fun stopTimer() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        Log.d(TAG, "Stopped unlock timer service")
    }
    
    private fun checkExpiredSessions() {
        serviceScope.launch {
            try {
                val activeSessionsResult = sessionRepository.getActiveSessions()
                activeSessionsResult.collect { sessions ->
                    sessions.forEach { session ->
                        if (session.isExpired) {
                            // End the expired session
                            sessionRepository.endSession(session.id, System.currentTimeMillis())
                            Log.d(TAG, "Ended expired session for ${session.packageName}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking expired sessions", e)
            }
        }
    }
}
