package com.stepunlock.domain.repository

import com.stepunlock.core.Result
import com.stepunlock.domain.model.AppRule
import kotlinx.coroutines.flow.Flow

interface AppRuleRepository {
    fun getAllAppRules(): Flow<List<AppRule>>
    fun getLockedApps(): Flow<List<AppRule>>
    suspend fun getAppRule(packageName: String): Result<AppRule?>
    fun getAppRuleFlow(packageName: String): Flow<AppRule?>
    suspend fun insertAppRule(appRule: AppRule): Result<Unit>
    suspend fun insertAppRules(appRules: List<AppRule>): Result<Unit>
    suspend fun updateAppRule(appRule: AppRule): Result<Unit>
    suspend fun deleteAppRule(appRule: AppRule): Result<Unit>
    suspend fun updateLockStatus(packageName: String, locked: Boolean): Result<Unit>
    suspend fun updateUnlockSettings(packageName: String, cost: Int, minutes: Int): Result<Unit>
}
