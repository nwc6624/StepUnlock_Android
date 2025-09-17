package com.stepunlock.app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.stepunlock.app.data.dao.AppRuleDao
import com.stepunlock.app.data.dao.CreditTransactionDao
import com.stepunlock.app.data.dao.HabitProgressDao
import com.stepunlock.app.data.database.StepUnlockDatabase
import com.stepunlock.app.data.model.AppRule
import com.stepunlock.app.data.model.CreditTransaction
import com.stepunlock.app.data.model.HabitProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class StepUnlockRepository(context: Context) {
    private val database = StepUnlockDatabase.getDatabase(context)
    private val appRuleDao = database.appRuleDao()
    private val creditTransactionDao = database.creditTransactionDao()
    private val habitProgressDao = database.habitProgressDao()
    private val packageManager = context.packageManager

    // App Rules
    suspend fun getAllApps(): Flow<List<AppRule>> = appRuleDao.getAllApps()
    suspend fun getLockedApps(): Flow<List<AppRule>> = appRuleDao.getLockedApps()
    suspend fun getAppByPackageName(packageName: String): AppRule? = appRuleDao.getAppByPackageName(packageName)
    suspend fun insertApp(appRule: AppRule) = appRuleDao.insertApp(appRule)
    suspend fun updateApp(appRule: AppRule) = appRuleDao.updateApp(appRule)
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean) = appRuleDao.updateLockStatus(packageName, isLocked)
    suspend fun updateUnlockCost(packageName: String, cost: Int) = appRuleDao.updateUnlockCost(packageName, cost)

    // Credit Transactions
    suspend fun getAllTransactions(): Flow<List<CreditTransaction>> = creditTransactionDao.getAllTransactions()
    suspend fun getTransactionsByType(type: String): Flow<List<CreditTransaction>> = creditTransactionDao.getTransactionsByType(type)
    suspend fun getCurrentBalance(): Int {
        val balance = creditTransactionDao.getCurrentBalance()
        android.util.Log.d("StepUnlockRepository", "Current balance: $balance")
        return balance
    }
    suspend fun insertTransaction(transaction: CreditTransaction) = creditTransactionDao.insertTransaction(transaction)
    suspend fun earnCredits(amount: Int, description: String) {
        android.util.Log.d("StepUnlockRepository", "Earning $amount credits: $description")
        val transaction = CreditTransaction(
            amount = amount,
            type = "EARNED",
            description = description
        )
        insertTransaction(transaction)
        android.util.Log.d("StepUnlockRepository", "Credits earned successfully")
    }
    suspend fun spendCredits(amount: Int, description: String): Boolean {
        val currentBalance = getCurrentBalance()
        if (currentBalance >= amount) {
            val transaction = CreditTransaction(
                amount = -amount,
                type = "SPENT",
                description = description
            )
            insertTransaction(transaction)
            return true
        }
        return false
    }

    // Habit Progress
    suspend fun getAllProgress(): Flow<List<HabitProgress>> = habitProgressDao.getAllProgress()
    suspend fun getProgressForDate(date: String): Flow<List<HabitProgress>> = habitProgressDao.getProgressForDate(date)
    fun getHabitProgress(habitType: String, date: String): Flow<HabitProgress?> = habitProgressDao.getHabitProgress(habitType, date)
    suspend fun insertProgress(progress: HabitProgress) = habitProgressDao.insertProgress(progress)
    suspend fun updateProgress(progress: HabitProgress) = habitProgressDao.updateProgress(progress)
    suspend fun updateHabitProgress(habitType: String, date: String, value: Int, completed: Boolean) {
        habitProgressDao.updateHabitProgress(habitType, date, value, completed)
    }
    
    suspend fun updateHabitProgress(progress: HabitProgress) {
        habitProgressDao.updateProgress(progress)
    }
    
    fun getTotalCredits(): Flow<Int?> = creditTransactionDao.getTotalCredits()

    // Initialize default data
    suspend fun initializeDefaultData() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        // Initialize default habit progress
        val defaultHabits = listOf(
            HabitProgress(today, "steps", 10000, 0, false),
            HabitProgress(today, "pomodoro", 4, 0, false),
            HabitProgress(today, "water", 8, 0, false),
            HabitProgress(today, "journaling", 1, 0, false)
        )
        
        for (habit in defaultHabits) {
            val existingProgress = getHabitProgress(habit.habitType, habit.date).firstOrNull()
            if (existingProgress == null) {
                insertProgress(habit)
            }
        }
        
        // Initialize with some starting credits
        if (getCurrentBalance() == 0) {
            earnCredits(100, "Welcome bonus")
        }
    }

    // Detect and sync installed apps
    suspend fun syncInstalledApps() {
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        for (appInfo in installedApps) {
            // Filter out system apps and our own app
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && 
                appInfo.packageName != "com.stepunlock.app") {
                
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val category = categorizeApp(appInfo.packageName, appName)
                
                val existingApp = getAppByPackageName(appInfo.packageName)
                if (existingApp == null) {
                    val appRule = AppRule(
                        packageName = appInfo.packageName,
                        appName = appName,
                        isLocked = isAppLockedByDefault(category),
                        unlockCost = getUnlockCost(category),
                        category = category.name
                    )
                    insertApp(appRule)
                }
            }
        }
    }

    private fun categorizeApp(packageName: String, appName: String): AppCategory {
        val socialKeywords = listOf("facebook", "instagram", "twitter", "tiktok", "snapchat", "whatsapp", "telegram", "discord", "messenger")
        val productivityKeywords = listOf("office", "word", "excel", "powerpoint", "notion", "trello", "slack", "zoom", "teams")
        val entertainmentKeywords = listOf("youtube", "netflix", "spotify", "twitch", "hulu", "disney", "prime", "music", "video")
        val gameKeywords = listOf("game", "play", "candy", "clash", "pokemon", "minecraft", "roblox", "among", "pubg")
        val utilityKeywords = listOf("camera", "gallery", "calculator", "calendar", "clock", "weather", "maps", "browser", "file")
        
        val lowerPackage = packageName.lowercase()
        val lowerName = appName.lowercase()
        
        return when {
            socialKeywords.any { lowerPackage.contains(it) || lowerName.contains(it) } -> AppCategory.SOCIAL
            productivityKeywords.any { lowerPackage.contains(it) || lowerName.contains(it) } -> AppCategory.PRODUCTIVITY
            entertainmentKeywords.any { lowerPackage.contains(it) || lowerName.contains(it) } -> AppCategory.ENTERTAINMENT
            gameKeywords.any { lowerPackage.contains(it) || lowerName.contains(it) } -> AppCategory.GAMES
            utilityKeywords.any { lowerPackage.contains(it) || lowerName.contains(it) } -> AppCategory.UTILITIES
            else -> AppCategory.OTHER
        }
    }

    private fun isAppLockedByDefault(category: AppCategory): Boolean {
        return category == AppCategory.SOCIAL || category == AppCategory.ENTERTAINMENT || category == AppCategory.GAMES
    }

    private fun getUnlockCost(category: AppCategory): Int {
        return when (category) {
            AppCategory.SOCIAL -> 15
            AppCategory.ENTERTAINMENT -> 20
            AppCategory.GAMES -> 25
            AppCategory.PRODUCTIVITY -> 5
            AppCategory.UTILITIES -> 3
            AppCategory.OTHER -> 10
        }
    }
}

enum class AppCategory {
    SOCIAL, PRODUCTIVITY, ENTERTAINMENT, GAMES, UTILITIES, OTHER
}
