package com.stepunlock.data.mapper

import com.stepunlock.data.local.entities.SessionEntity
import com.stepunlock.domain.model.Session

object SessionMapper {
    
    fun toDomain(entity: SessionEntity): Session {
        return Session(
            id = entity.id,
            packageName = entity.packageName,
            appName = entity.appName,
            startTime = entity.startTime,
            endTime = entity.endTime,
            grantedMinutes = entity.grantedMinutes,
            creditsSpent = entity.creditsSpent,
            isActive = entity.isActive
        )
    }
    
    fun toEntity(domain: Session): SessionEntity {
        return SessionEntity(
            id = domain.id,
            packageName = domain.packageName,
            appName = domain.appName,
            startTime = domain.startTime,
            endTime = domain.endTime,
            grantedMinutes = domain.grantedMinutes,
            creditsSpent = domain.creditsSpent,
            isActive = domain.isActive
        )
    }
}
