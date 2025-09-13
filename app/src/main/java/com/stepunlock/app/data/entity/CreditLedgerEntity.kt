package com.stepunlock.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "credit_ledger")
data class CreditLedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int, // positive for earning, negative for spending
    val reason: String, // "steps_10000", "pomodoro_session", "unlock_app", etc.
    val timestamp: Long = System.currentTimeMillis(),
    val appPackageName: String? = null, // for app unlock transactions
    val habitType: String? = null // for habit completion transactions
)
