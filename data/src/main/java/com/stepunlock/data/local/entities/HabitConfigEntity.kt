package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_configs")
data class HabitConfigEntity(
    @PrimaryKey
    val id: String, // e.g., "steps", "pomodoro", "water", "journal"
    val name: String,
    val enabled: Boolean = true,
    val earnRate: Int, // credits per unit; e.g., 2 credits per 1000 steps
    val goalPerDay: Int? = null, // for streaks
    val unit: String, // e.g., "steps", "sessions", "glasses", "minutes"
    val iconName: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
