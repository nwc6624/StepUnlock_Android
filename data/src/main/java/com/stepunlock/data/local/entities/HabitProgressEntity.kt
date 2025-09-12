package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit_progress")
data class HabitProgressEntity(
    @PrimaryKey
    val date: String, // Format: "yyyy-MM-dd"
    val stepsCount: Int = 0,
    val stepsTarget: Int = 10000,
    val pomodorosCompleted: Int = 0,
    val pomodorosTarget: Int = 4,
    val waterGlasses: Int = 0,
    val waterTarget: Int = 8,
    val journalEntries: Int = 0,
    val journalTarget: Int = 1,
    val customHabitsCompleted: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
