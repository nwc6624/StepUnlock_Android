package com.stepunlock.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_rules")
data class AppRuleEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isLocked: Boolean = true,
    val unlockCost: Int = 10, // credits needed to unlock
    val unlockDuration: Long = 15 * 60 * 1000, // 15 minutes in milliseconds
    val isUnlocked: Boolean = false,
    val unlockedUntil: Long = 0, // timestamp when unlock expires
    val iconUri: String? = null,
    val category: String = "Social", // Social, Entertainment, Games, etc.
    val lastUsed: Long = 0,
    val usageCount: Int = 0
)
