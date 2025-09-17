package com.stepunlock.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_progress")
data class HabitProgress(
    @PrimaryKey
    val date: String, // YYYY-MM-DD format
    val habitType: String, // "steps", "pomodoro", "water", "journaling"
    val targetValue: Int,
    val currentValue: Int = 0,
    val isCompleted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis(),
    val streakCount: Int = 0
)
