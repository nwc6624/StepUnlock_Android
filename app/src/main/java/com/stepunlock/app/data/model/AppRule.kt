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

@Entity(tableName = "credit_transactions")
data class CreditTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int,
    val type: String, // "EARNED" or "SPENT"
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_progress")
data class HabitProgress(
    @PrimaryKey
    val id: String, // "habitType_date" format
    val habitType: String,
    val date: String,
    val currentValue: Int = 0,
    val targetValue: Int,
    val isCompleted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
