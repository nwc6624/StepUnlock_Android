package com.stepunlock.app.data.dao

import androidx.room.*
import com.stepunlock.app.data.entity.AppRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {
    
    @Query("SELECT * FROM app_rules WHERE isLocked = 1")
    fun getLockedApps(): Flow<List<AppRuleEntity>>
    
    @Query("SELECT * FROM app_rules")
    fun getAllApps(): Flow<List<AppRuleEntity>>
    
    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    suspend fun getAppByPackage(packageName: String): AppRuleEntity?
    
    @Query("SELECT COUNT(*) FROM app_rules WHERE isLocked = 1")
    fun getLockedAppsCount(): Flow<Int>
    
    @Query("SELECT * FROM app_rules WHERE isUnlocked = 1 AND unlockedUntil > :currentTime")
    suspend fun getCurrentlyUnlockedApps(currentTime: Long = System.currentTimeMillis()): List<AppRuleEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppRuleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppRuleEntity>)
    
    @Update
    suspend fun updateApp(app: AppRuleEntity)
    
    @Query("UPDATE app_rules SET isLocked = :isLocked WHERE packageName = :packageName")
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean)
    
    @Query("UPDATE app_rules SET isUnlocked = :isUnlocked, unlockedUntil = :unlockedUntil WHERE packageName = :packageName")
    suspend fun updateUnlockStatus(packageName: String, isUnlocked: Boolean, unlockedUntil: Long)
    
    @Query("UPDATE app_rules SET lastUsed = :timestamp, usageCount = usageCount + 1 WHERE packageName = :packageName")
    suspend fun updateUsageStats(packageName: String, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteApp(app: AppRuleEntity)
    
    @Query("DELETE FROM app_rules")
    suspend fun deleteAllApps()
}
