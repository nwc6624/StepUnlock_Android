package com.stepunlock.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_rules")
data class AppRule(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isLocked: Boolean = false,
    val unlockCost: Int = 10,
    val category: String = "OTHER"
)
