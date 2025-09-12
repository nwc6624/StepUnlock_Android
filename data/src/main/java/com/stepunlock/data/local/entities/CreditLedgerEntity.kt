package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_ledger")
data class CreditLedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "EARNED" or "SPENT"
    val habitType: String? = null,
    val appPackageName: String? = null
)