package com.stepunlock.app.data.models

data class Wallet(
    val balance: Int = 0,
    val totalEarned: Int = 0,
    val totalSpent: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
