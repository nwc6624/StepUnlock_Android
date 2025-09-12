package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_rules")
data class AppRuleEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val iconUri: String? = null,
    val creditsPerMinute: Int = 10,
    val isLocked: Boolean = true,
    val isEnabled: Boolean = true,
    val lastUsed: Long = 0L,
    val totalUsageMinutes: Long = 0L,
    val createdAt: Long = System.currentTimeMillis()
)