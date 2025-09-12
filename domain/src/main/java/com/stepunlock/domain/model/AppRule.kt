package com.stepunlock.domain.model

data class AppRule(
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
