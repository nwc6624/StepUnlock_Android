package com.stepunlock.data.repository

import com.stepunlock.data.local.dao.AppRuleDao
import com.stepunlock.data.local.entities.AppRuleEntity
import com.stepunlock.domain.repository.AppRepository
import com.stepunlock.domain.repository.LockedApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val appRuleDao: AppRuleDao
) : AppRepository {
    
    override fun getLockedAppsCount(): Flow<Int> {
        return appRuleDao.getLockedAppsCount()
    }
    
    override suspend fun getLockedApps(): Flow<List<LockedApp>> {
        return appRuleDao.getAllLockedApps().map { entities ->
            entities.map { entity ->
                LockedApp(
                    packageName = entity.packageName,
                    appName = entity.appName,
                    icon = entity.iconUri,
                    creditsPerMinute = entity.creditsPerMinute,
                    isLocked = entity.isLocked,
                    lastUsed = entity.lastUsed
                )
            }
        }
    }
    
    override suspend fun addAppToLocked(app: LockedApp) {
        val entity = AppRuleEntity(
            packageName = app.packageName,
            appName = app.appName,
            iconUri = app.icon,
            creditsPerMinute = app.creditsPerMinute,
            isLocked = app.isLocked,
            isEnabled = true,
            lastUsed = app.lastUsed
        )
        appRuleDao.insertAppRule(entity)
    }
    
    override suspend fun removeAppFromLocked(packageName: String) {
        appRuleDao.deleteAppRuleByPackage(packageName)
    }
    
    override suspend fun updateAppRule(packageName: String, creditsPerMinute: Int) {
        val existingRule = appRuleDao.getAppRule(packageName)
        if (existingRule != null) {
            val updatedRule = existingRule.copy(creditsPerMinute = creditsPerMinute)
            appRuleDao.updateAppRule(updatedRule)
        }
    }
}
