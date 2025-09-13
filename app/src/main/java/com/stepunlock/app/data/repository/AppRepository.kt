package com.stepunlock.app.data.repository

import com.stepunlock.app.data.entity.AppRuleEntity
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getLockedApps(): Flow<List<AppRuleEntity>>
    fun getAllApps(): Flow<List<AppRuleEntity>>
    suspend fun getAppByPackage(packageName: String): AppRuleEntity?
    fun getLockedAppsCount(): Flow<Int>
    suspend fun getCurrentlyUnlockedApps(): List<AppRuleEntity>
    suspend fun insertApp(app: AppRuleEntity)
    suspend fun insertApps(apps: List<AppRuleEntity>)
    suspend fun updateApp(app: AppRuleEntity)
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean)
    suspend fun updateUnlockStatus(packageName: String, isUnlocked: Boolean, unlockedUntil: Long)
    suspend fun updateUsageStats(packageName: String)
    suspend fun deleteApp(app: AppRuleEntity)
    suspend fun deleteAllApps()
}
