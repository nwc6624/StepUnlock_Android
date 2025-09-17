package com.stepunlock.app.data.repository

import android.content.Context
import com.stepunlock.app.data.model.HabitProgress
import com.stepunlock.app.services.GoogleFitService
import com.stepunlock.app.services.StepTrackingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class StepTrackingRepository(private val context: Context) {
    
    private val googleFitService = GoogleFitService(context)
    private val stepUnlockRepository = StepUnlockRepository(context)
    
    private val _currentSteps = MutableStateFlow(0)
    val currentSteps: StateFlow<Int> = _currentSteps.asStateFlow()
    
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()
    
    private val _isGoogleFitConnected = MutableStateFlow(false)
    val isGoogleFitConnected: StateFlow<Boolean> = _isGoogleFitConnected.asStateFlow()
    
    suspend fun initialize() {
        android.util.Log.d("StepTrackingRepository", "Initializing StepTrackingRepository")
        // Check Google Fit connection
        _isGoogleFitConnected.value = googleFitService.isConnected()
        
        // Load today's steps from database
        loadTodaySteps()
        android.util.Log.d("StepTrackingRepository", "StepTrackingRepository initialized")
    }
    
    suspend fun startTracking() {
        try {
            android.util.Log.d("StepTrackingRepository", "Starting step tracking (mock mode)")
            // For now, use mock tracking without service to avoid permission issues
            _isTracking.value = true
            android.util.Log.d("StepTrackingRepository", "Step tracking started (mock mode)")
            
            // Load initial step count
            loadTodaySteps()
            
            // Start mock step tracking
            startMockStepTracking()
        } catch (e: Exception) {
            android.util.Log.e("StepTrackingRepository", "Error starting step tracking", e)
        }
    }
    
    private fun startMockStepTracking() {
        // Start a simple mock step tracking without service
        GlobalScope.launch {
            var mockSteps = 0
            while (_isTracking.value) {
                mockSteps += (1..5).random() // Add 1-5 steps every 30 seconds
                _currentSteps.value = mockSteps
                android.util.Log.d("StepTrackingRepository", "Mock steps updated: $mockSteps")
                
                // Update database
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                try {
                    updateDatabaseSteps(today, mockSteps)
                } catch (e: Exception) {
                    android.util.Log.e("StepTrackingRepository", "Error updating database", e)
                }
                
                delay(30000) // 30 seconds
            }
        }
    }
    
    suspend fun stopTracking() {
        try {
            _isTracking.value = false
            android.util.Log.d("StepTrackingRepository", "Step tracking stopped")
        } catch (e: Exception) {
            android.util.Log.e("StepTrackingRepository", "Error stopping step tracking", e)
        }
    }
    
    suspend fun loadTodaySteps() {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // Try to get steps from Google Fit first
            if (_isGoogleFitConnected.value) {
                val googleFitSteps = googleFitService.getTodaySteps()
                if (googleFitSteps > 0) {
                    _currentSteps.value = googleFitSteps
                    updateDatabaseSteps(today, googleFitSteps)
                    return
                }
            }
            
            // Fallback to database
            val habitProgress = stepUnlockRepository.getHabitProgress("steps", today).firstOrNull()
            _currentSteps.value = habitProgress?.currentValue ?: 0
            
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun refreshSteps() {
        loadTodaySteps()
    }
    
    suspend fun connectGoogleFit(): Boolean {
        return try {
            val connected = googleFitService.connect()
            _isGoogleFitConnected.value = connected
            if (connected) {
                loadTodaySteps() // Refresh steps from Google Fit
            }
            connected
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun disconnectGoogleFit() {
        try {
            googleFitService.disconnect()
            _isGoogleFitConnected.value = false
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun getGoogleFitSignInIntent() = googleFitService.getSignInIntent()
    
    suspend fun updateSteps(newStepCount: Int) {
        try {
            _currentSteps.value = newStepCount
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            updateDatabaseSteps(today, newStepCount)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    private suspend fun updateDatabaseSteps(date: String, stepCount: Int) {
        try {
            val habitProgress = HabitProgress(
                date = date,
                habitType = "steps",
                targetValue = 10000,
                currentValue = stepCount,
                isCompleted = stepCount >= 10000,
                lastUpdated = System.currentTimeMillis(),
                streakCount = 0 // TODO: Calculate actual streak
            )
            
            stepUnlockRepository.updateHabitProgress(habitProgress)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    suspend fun getStepsForDate(date: Date): Int {
        return try {
            if (_isGoogleFitConnected.value) {
                googleFitService.getStepsForDate(date)
            } else {
                val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                stepUnlockRepository.getHabitProgress("steps", dateString).firstOrNull()?.currentValue ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }
    
    suspend fun getWeeklySteps(): List<Int> {
        return try {
            val calendar = Calendar.getInstance()
            val weeklySteps = mutableListOf<Int>()
            
            // Get steps for the last 7 days
            repeat(7) { i ->
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val steps = getStepsForDate(calendar.time)
                weeklySteps.add(0, steps) // Add to beginning to maintain chronological order
                calendar.add(Calendar.DAY_OF_YEAR, i) // Reset calendar
            }
            
            weeklySteps
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getMonthlySteps(): List<Int> {
        return try {
            val calendar = Calendar.getInstance()
            val monthlySteps = mutableListOf<Int>()
            
            // Get steps for the last 30 days
            repeat(30) { i ->
                calendar.add(Calendar.DAY_OF_YEAR, -i)
                val steps = getStepsForDate(calendar.time)
                monthlySteps.add(0, steps)
                calendar.add(Calendar.DAY_OF_YEAR, i)
            }
            
            monthlySteps
        } catch (e: Exception) {
            emptyList()
        }
    }
}
