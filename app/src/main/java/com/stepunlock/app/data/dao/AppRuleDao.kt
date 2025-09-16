package com.stepunlock.app.data.dao

import androidx.room.*
import com.stepunlock.app.data.model.AppRule
import kotlinx.coroutines.flow.Flow

@Dao
interface AppRuleDao {
    @Query("SELECT * FROM app_rules ORDER BY appName ASC")
    fun getAllApps(): Flow<List<AppRule>>

    @Query("SELECT * FROM app_rules WHERE isLocked = 1")
    fun getLockedApps(): Flow<List<AppRule>>

    @Query("SELECT * FROM app_rules WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): AppRule?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(appRule: AppRule)

    @Update
    suspend fun updateApp(appRule: AppRule)

    @Delete
    suspend fun deleteApp(appRule: AppRule)

    @Query("UPDATE app_rules SET isLocked = :isLocked WHERE packageName = :packageName")
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean)

    @Query("UPDATE app_rules SET unlockCost = :cost WHERE packageName = :packageName")
    suspend fun updateUnlockCost(packageName: String, cost: Int)
}