package com.stepunlock.app.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.stepunlock.app.MainActivity
import com.stepunlock.app.R
import com.stepunlock.app.data.repository.StepUnlockRepository
import com.stepunlock.app.data.model.HabitProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StepTrackingService : Service(), SensorEventListener {
    
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private lateinit var repository: StepUnlockRepository
    
    private var stepCount = 0
    private var lastStepCount = 0
    private var isTracking = false
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "step_tracking_channel"
        private const val UPDATE_INTERVAL = 30000L // 30 seconds
        
        fun startService(context: Context) {
            val intent = Intent(context, StepTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopService(context: Context) {
            val intent = Intent(context, StepTrackingService::class.java)
            context.stopService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("StepTrackingService", "StepTrackingService onCreate called")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        repository = StepUnlockRepository(this)
        
        // Get step counter sensor (hardware-based, more accurate)
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        // Get step detector sensor (event-based, less accurate but more responsive)
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        startStepTracking()
        android.util.Log.d("StepTrackingService", "StepTrackingService onCreate completed")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Restart service if killed
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Step Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracks your daily steps for StepUnlock"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
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
            .setContentTitle("StepUnlock - Step Tracking")
            .setContentText("Tracking your steps: $stepCount")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
    
    private fun startStepTracking() {
        // For now, use mock data to avoid health service classification issues
        // TODO: Re-enable real sensor tracking once permissions are properly configured
        startMockStepTracking()
        
        // Start periodic updates
        serviceScope.launch {
            while (isTracking) {
                updateStepCount()
                delay(UPDATE_INTERVAL)
            }
        }
    }
    
    private fun startMockStepTracking() {
        // For testing on emulator or devices without step sensors
        android.util.Log.d("StepTrackingService", "Starting mock step tracking")
        isTracking = true
        serviceScope.launch {
            var mockSteps = 0
            while (isTracking) {
                mockSteps += (1..5).random() // Add 1-5 steps every 30 seconds
                stepCount = mockSteps
                android.util.Log.d("StepTrackingService", "Mock steps updated: $stepCount")
                updateStepCount()
                delay(UPDATE_INTERVAL)
            }
        }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            when (sensorEvent.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    // Step counter gives total steps since last reboot
                    val totalSteps = sensorEvent.values[0].toInt()
                    if (lastStepCount == 0) {
                        lastStepCount = totalSteps
                    }
                    stepCount = totalSteps - lastStepCount
                }
                Sensor.TYPE_STEP_DETECTOR -> {
                    // Step detector gives individual step events
                    stepCount++
                }
                else -> {
                    // Handle other sensor types if needed
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
    
    private suspend fun updateStepCount() {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // Update database with current step count
            repository.updateHabitProgress(
                HabitProgress(
                    date = today,
                    habitType = "steps",
                    targetValue = 10000,
                    currentValue = stepCount,
                    isCompleted = stepCount >= 10000,
                    lastUpdated = System.currentTimeMillis(),
                    streakCount = 0 // TODO: Calculate actual streak
                )
            )
            
            // Update notification
            val notification = createNotification()
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
            
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isTracking = false
        sensorManager.unregisterListener(this)
    }
}
