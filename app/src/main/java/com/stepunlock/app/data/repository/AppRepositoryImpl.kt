package com.stepunlock.app.data.repository

import com.stepunlock.app.data.dao.AppRuleDao
import com.stepunlock.app.data.entity.AppRuleEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appRuleDao: AppRuleDao
) : AppRepository {
    
    override fun getLockedApps(): Flow<List<AppRuleEntity>> = appRuleDao.getLockedApps()
    
    override fun getAllApps(): Flow<List<AppRuleEntity>> = appRuleDao.getAllApps()
    
    override suspend fun getAppByPackage(packageName: String): AppRuleEntity? = 
        appRuleDao.getAppByPackage(packageName)
    
    override fun getLockedAppsCount(): Flow<Int> = appRuleDao.getLockedAppsCount()
    
    override suspend fun getCurrentlyUnlockedApps(): List<AppRuleEntity> = 
        appRuleDao.getCurrentlyUnlockedApps()
    
    override suspend fun insertApp(app: AppRuleEntity) = appRuleDao.insertApp(app)
    
    override suspend fun insertApps(apps: List<AppRuleEntity>) = appRuleDao.insertApps(apps)
    
    override suspend fun updateApp(app: AppRuleEntity) = appRuleDao.updateApp(app)
    
    override suspend fun updateLockStatus(packageName: String, isLocked: Boolean) = 
        appRuleDao.updateLockStatus(packageName, isLocked)
    
    override suspend fun updateUnlockStatus(packageName: String, isUnlocked: Boolean, unlockedUntil: Long) = 
        appRuleDao.updateUnlockStatus(packageName, isUnlocked, unlockedUntil)
    
    override suspend fun updateUsageStats(packageName: String) = 
        appRuleDao.updateUsageStats(packageName)
    
    override suspend fun deleteApp(app: AppRuleEntity) = appRuleDao.deleteApp(app)
    
    override suspend fun deleteAllApps() = appRuleDao.deleteAllApps()
}
