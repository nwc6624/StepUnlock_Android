package com.stepunlock.app.data

import android.content.Context
import com.stepunlock.app.data.entity.AppRuleEntity
import com.stepunlock.app.data.entity.CreditLedgerEntity
import com.stepunlock.app.data.entity.HabitProgressEntity
import com.stepunlock.app.data.repository.AppRepository
import com.stepunlock.app.data.repository.CreditRepository
import com.stepunlock.app.data.repository.HabitRepository
import com.stepunlock.app.utils.AppDetectionUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appRepository: AppRepository,
    private val creditRepository: CreditRepository,
    private val habitRepository: HabitRepository
) {
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    suspend fun initializeData() = withContext(Dispatchers.IO) {
        try {
            // Initialize default apps
            initializeDefaultApps()
            
            // Initialize default credit balance
            initializeDefaultCredits()
            
            // Initialize today's habit progress
            initializeTodayHabits()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private suspend fun initializeDefaultApps() {
        // Check if apps are already initialized
        val existingApps = appRepository.getAllApps()
        val appList = existingApps.collect { apps ->
            if (apps.isEmpty()) {
                // Get installed apps and convert to AppRuleEntity
                val installedApps = AppDetectionUtils.getInstalledApps(context)
                val appRules = AppDetectionUtils.convertToAppRuleEntities(installedApps)
                
                // Insert apps into database
                appRepository.insertApps(appRules)
            }
        }
    }
    
    private suspend fun initializeDefaultCredits() {
        // Check if user already has credits
        val totalCredits = creditRepository.getTotalCredits()
        if (totalCredits == 0) {
            // Give user starting credits
            creditRepository.insertTransaction(
                CreditLedgerEntity(
                    amount = 50,
                    reason = "welcome_bonus",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
    
    private suspend fun initializeTodayHabits() {
        val today = dateFormatter.format(Date())
        
        val habitTypes = listOf("steps", "pomodoro", "water", "journaling")
        val defaultTargets = mapOf(
            "steps" to 10000,
            "pomodoro" to 4,
            "water" to 8,
            "journaling" to 1
        )
        
        for (habitType in habitTypes) {
            val existingProgress = habitRepository.getHabitProgress(habitType, today)
            if (existingProgress == null) {
                val progress = HabitProgressEntity(
                    date = today,
                    habitType = habitType,
                    targetValue = defaultTargets[habitType] ?: 1,
                    currentValue = 0,
                    isCompleted = false,
                    lastUpdated = System.currentTimeMillis(),
                    streakCount = 0
                )
                habitRepository.insertProgress(progress)
            }
        }
    }
}
