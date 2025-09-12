package com.stepunlock.domain.model

data class Session(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val startTime: Long,
    val endTime: Long? = null,
    val grantedMinutes: Int,
    val creditsSpent: Int,
    val isActive: Boolean = true
) {
    val duration: Long
        get() = (endTime ?: System.currentTimeMillis()) - startTime
    
    val remainingMinutes: Int
        get() = if (isActive) {
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = (grantedMinutes * 60 * 1000L) - elapsed
            maxOf(0, (remaining / (60 * 1000)).toInt())
        } else {
            0
        }
    
    val isExpired: Boolean
        get() = isActive && remainingMinutes <= 0
}
