package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey
    val habitId: String,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0,
    val lastEarnedDay: Long = 0L, // timestamp of last day when habit was completed
    val streakStartDate: Long = 0L, // when current streak started
    val updatedAt: Long = System.currentTimeMillis()
)
