package com.stepunlock.data.repository

import com.stepunlock.core.Result
import com.stepunlock.data.local.dao.AppRuleDao
import com.stepunlock.data.mapper.AppRuleMapper
import com.stepunlock.domain.model.AppRule
import com.stepunlock.domain.repository.AppRuleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRuleRepositoryImpl @Inject constructor(
    private val appRuleDao: AppRuleDao
) : AppRuleRepository {
    
    override fun getAllAppRules(): Flow<List<AppRule>> {
        return appRuleDao.getAllAppRules().map { entities ->
            entities.map { AppRuleMapper.toDomain(it) }
        }
    }
    
    override fun getLockedApps(): Flow<List<AppRule>> {
        return appRuleDao.getLockedApps().map { entities ->
            entities.map { AppRuleMapper.toDomain(it) }
        }
    }
    
    override suspend fun getAppRule(packageName: String): Result<AppRule?> {
        return try {
            val entity = appRuleDao.getAppRule(packageName)
            Result.Success(entity?.let { AppRuleMapper.toDomain(it) })
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override fun getAppRuleFlow(packageName: String): Flow<AppRule?> {
        return appRuleDao.getAppRuleFlow(packageName).map { entity ->
            entity?.let { AppRuleMapper.toDomain(it) }
        }
    }
    
    override suspend fun insertAppRule(appRule: AppRule): Result<Unit> {
        return try {
            appRuleDao.insertAppRule(AppRuleMapper.toEntity(appRule))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun insertAppRules(appRules: List<AppRule>): Result<Unit> {
        return try {
            appRuleDao.insertAppRules(appRules.map { AppRuleMapper.toEntity(it) })
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateAppRule(appRule: AppRule): Result<Unit> {
        return try {
            appRuleDao.updateAppRule(AppRuleMapper.toEntity(appRule))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun deleteAppRule(appRule: AppRule): Result<Unit> {
        return try {
            appRuleDao.deleteAppRule(AppRuleMapper.toEntity(appRule))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateLockStatus(packageName: String, locked: Boolean): Result<Unit> {
        return try {
            appRuleDao.updateLockStatus(packageName, locked)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun updateUnlockSettings(packageName: String, cost: Int, minutes: Int): Result<Unit> {
        return try {
            appRuleDao.updateUnlockSettings(packageName, cost, minutes)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
