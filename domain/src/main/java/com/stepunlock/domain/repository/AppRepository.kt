package com.stepunlock.domain.repository

import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getLockedAppsCount(): Flow<Int>
    suspend fun getLockedApps(): Flow<List<LockedApp>>
    suspend fun addAppToLocked(app: LockedApp)
    suspend fun removeAppFromLocked(packageName: String)
    suspend fun updateAppRule(packageName: String, creditsPerMinute: Int)
}

data class LockedApp(
    val packageName: String,
    val appName: String,
    val icon: String? = null,
    val creditsPerMinute: Int = 10,
    val isLocked: Boolean = true,
    val lastUsed: Long = 0L
)
