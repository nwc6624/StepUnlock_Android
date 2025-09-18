package com.stepunlock.app.data.models

import java.util.*

data class User(
    val id: String,
    val timezone: String = TimeZone.getDefault().id,
    val streaks: Map<String, Int> = emptyMap(), // habitId -> streak count
    val createdAt: Date = Date()
)
