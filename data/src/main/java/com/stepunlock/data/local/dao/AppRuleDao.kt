package com.stepunlock.data.local.dao

import androidx.room.*
import com.stepunlock.data.local.entities.AppRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {
    
    @Query("SELECT * FROM app_rules ORDER BY appName ASC")
    fun getAllAppRules(): Flow<List<AppRuleEntity>>
    
    @Query("SELECT * FROM app_rules WHERE locked = 1 ORDER BY appName ASC")
    fun getLockedApps(): Flow<List<AppRuleEntity>>
    
    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    suspend fun getAppRule(packageName: String): AppRuleEntity?
    
    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    fun getAppRuleFlow(packageName: String): Flow<AppRuleEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppRule(appRule: AppRuleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppRules(appRules: List<AppRuleEntity>)
    
    @Update
    suspend fun updateAppRule(appRule: AppRuleEntity)
    
    @Delete
    suspend fun deleteAppRule(appRule: AppRuleEntity)
    
    @Query("DELETE FROM app_rules WHERE packageName = :packageName")
    suspend fun deleteAppRuleByPackage(packageName: String)
    
    @Query("UPDATE app_rules SET locked = :locked, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateLockStatus(packageName: String, locked: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE app_rules SET unlockCostCredits = :cost, unlockMinutes = :minutes, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateUnlockSettings(packageName: String, cost: Int, minutes: Int, timestamp: Long = System.currentTimeMillis())
}
