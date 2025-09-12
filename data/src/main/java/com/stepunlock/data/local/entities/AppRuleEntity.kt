package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_rules")
data class AppRuleEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val locked: Boolean = false,
    val unlockCostCredits: Int = 10,
    val unlockMinutes: Int = 15,
    val category: String? = null,
    val iconUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
