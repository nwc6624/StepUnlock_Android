package com.stepunlock.app.data.models

import java.util.*

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Int,
    val source: String, // habitId for earn, rewardSlug for spend
    val meta: Map<String, Any> = emptyMap(),
    val createdAt: Date = Date(),
    val idempotencyKey: String
)

enum class TransactionType {
    EARN, SPEND
}
