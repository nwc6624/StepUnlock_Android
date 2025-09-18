package com.stepunlock.app.data.events

import java.util.*

data class EarnEvent(
    val type: String, // "steps", "focus_session", "hydration", "journaling"
    val units: Int? = null, // For habits that accept multiple units
    val amount: Int? = null, // Override credit amount if needed
    val idempotencyKey: String = UUID.randomUUID().toString(),
    val occurredAt: Date = Date(),
    val meta: Map<String, Any> = emptyMap()
)

data class SpendEvent(
    val rewardSlug: String,
    val cost: Int,
    val idempotencyKey: String = UUID.randomUUID().toString(),
    val occurredAt: Date = Date(),
    val meta: Map<String, Any> = emptyMap()
)
