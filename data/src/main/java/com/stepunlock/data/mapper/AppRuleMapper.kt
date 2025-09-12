package com.stepunlock.data.mapper

import com.stepunlock.data.local.entities.AppRuleEntity
import com.stepunlock.domain.model.AppRule

object AppRuleMapper {
    
    fun toDomain(entity: AppRuleEntity): AppRule {
        return AppRule(
            packageName = entity.packageName,
            appName = entity.appName,
            locked = entity.locked,
            unlockCostCredits = entity.unlockCostCredits,
            unlockMinutes = entity.unlockMinutes,
            category = entity.category,
            iconUri = entity.iconUri,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
    
    fun toEntity(domain: AppRule): AppRuleEntity {
        return AppRuleEntity(
            packageName = domain.packageName,
            appName = domain.appName,
            locked = domain.locked,
            unlockCostCredits = domain.unlockCostCredits,
            unlockMinutes = domain.unlockMinutes,
            category = domain.category,
            iconUri = domain.iconUri,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
