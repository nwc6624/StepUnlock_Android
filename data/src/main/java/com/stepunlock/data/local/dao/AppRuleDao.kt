package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.AppRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {
    @Query("SELECT * FROM app_rules WHERE isEnabled = 1 ORDER BY appName ASC")
    fun getAllLockedApps(): Flow<List<AppRuleEntity>>
    
    @Query("SELECT COUNT(*) FROM app_rules WHERE isEnabled = 1 AND isLocked = 1")
    fun getLockedAppsCount(): Flow<Int>
    
    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    suspend fun getAppRule(packageName: String): AppRuleEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppRule(appRule: AppRuleEntity)
    
    @Update
    suspend fun updateAppRule(appRule: AppRuleEntity)
    
    @Delete
    suspend fun deleteAppRule(appRule: AppRuleEntity)
    
    @Query("DELETE FROM app_rules WHERE packageName = :packageName")
    suspend fun deleteAppRuleByPackage(packageName: String)
    
    @Query("UPDATE app_rules SET isLocked = :isLocked WHERE packageName = :packageName")
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean)
    
    @Query("UPDATE app_rules SET lastUsed = :timestamp WHERE packageName = :packageName")
    suspend fun updateLastUsed(packageName: String, timestamp: Long)
    
    @Query("UPDATE app_rules SET totalUsageMinutes = totalUsageMinutes + :minutes WHERE packageName = :packageName")
    suspend fun addUsageMinutes(packageName: String, minutes: Long)
}