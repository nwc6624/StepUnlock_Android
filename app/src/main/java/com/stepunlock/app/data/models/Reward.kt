package com.stepunlock.app.data.models

data class Reward(
    val id: String,
    val slug: String,
    val name: String,
    val cost: Int,
    val status: RewardStatus = RewardStatus.LOCKED,
    val unlockedAt: Long? = null,
    val expiresAt: Long? = null // For temporary unlocks
)

enum class RewardStatus {
    LOCKED, UNLOCKED, EXPIRED
}
