package com.stepunlock.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_transactions")
data class CreditTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int, // Positive for earning, negative for spending
    val type: String, // "earned", "spent", "initial", etc.
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
