package com.stepunlock.data.mapper

import com.stepunlock.data.local.entities.HabitConfigEntity
import com.stepunlock.domain.model.HabitConfig

object HabitConfigMapper {
    
    fun toDomain(entity: HabitConfigEntity): HabitConfig {
        return HabitConfig(
            id = entity.id,
            name = entity.name,
            enabled = entity.enabled,
            earnRate = entity.earnRate,
            goalPerDay = entity.goalPerDay,
            unit = entity.unit,
            iconName = entity.iconName,
            color = entity.color,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: HabitConfig): HabitConfigEntity {
        return HabitConfigEntity(
            id = domain.id,
            name = domain.name,
            enabled = domain.enabled,
            earnRate = domain.earnRate,
            goalPerDay = domain.goalPerDay,
            unit = domain.unit,
            iconName = domain.iconName,
            color = domain.color,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
