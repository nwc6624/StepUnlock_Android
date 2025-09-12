package com.stepunlock.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val startTime: Long,
    val endTime: Long? = null,
    val grantedMinutes: Int,
    val creditsSpent: Int,
    val isActive: Boolean = true
)
