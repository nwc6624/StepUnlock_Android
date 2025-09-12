package com.stepunlock.data.mapper

import com.stepunlock.data.local.entities.StreakEntity
import com.stepunlock.domain.model.Streak

object StreakMapper {
    
    fun toDomain(entity: StreakEntity): Streak {
        return Streak(
            habitId = entity.habitId,
            currentStreakDays = entity.currentStreakDays,
            longestStreakDays = entity.longestStreakDays,
            lastEarnedDay = entity.lastEarnedDay,
            streakStartDate = entity.streakStartDate,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: Streak): StreakEntity {
        return StreakEntity(
            habitId = domain.habitId,
            currentStreakDays = domain.currentStreakDays,
            longestStreakDays = domain.longestStreakDays,
            lastEarnedDay = domain.lastEarnedDay,
            streakStartDate = domain.streakStartDate,
            updatedAt = domain.updatedAt
        )
    }
}
