package com.stepunlock.domain.model

data class CreditTransaction(
    val id: Long = 0,
    val delta: Int, // positive = earned, negative = spent
    val reason: String, // e.g., "steps", "pomodoro", "unlock:com.instagram.android"
    val timestamp: Long = System.currentTimeMillis(),
    val habitId: String? = null, // for tracking which habit earned the credits
    val metadata: String? = null // JSON string for additional data
)
