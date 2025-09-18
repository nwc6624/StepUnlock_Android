package com.stepunlock.app.data.models

data class Habit(
    val id: String,
    val name: String,
    val unit: String,
    val goalPerDay: Int,
    val creditValue: Int,
    val cooldownMinutes: Int = 0,
    val dailyCap: Int = 1,
    val isActive: Boolean = true
)

data class HabitProgress(
    val habitId: String,
    val date: String, // YYYY-MM-DD format
    val valueSoFar: Int = 0,
    val lastEarnedAt: Long = 0,
    val isCompleted: Boolean = false
)
