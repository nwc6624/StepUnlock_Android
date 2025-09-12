package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_ledger")
data class CreditLedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val delta: Int, // positive = earned, negative = spent
    val reason: String, // e.g., "steps", "pomodoro", "unlock:com.instagram.android"
    val timestamp: Long = System.currentTimeMillis(),
    val habitId: String? = null, // for tracking which habit earned the credits
    val metadata: String? = null // JSON string for additional data
)
