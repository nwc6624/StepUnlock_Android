package com.stepunlock.data.mapper

import com.stepunlock.data.local.entities.CreditLedgerEntity
import com.stepunlock.domain.model.CreditTransaction

object CreditTransactionMapper {
    
    fun toDomain(entity: CreditLedgerEntity): CreditTransaction {
        return CreditTransaction(
            id = entity.id,
            delta = entity.delta,
            reason = entity.reason,
            timestamp = entity.timestamp,
            habitId = entity.habitId,
            metadata = entity.metadata
        )
    }
    
    fun toEntity(domain: CreditTransaction): CreditLedgerEntity {
        return CreditLedgerEntity(
            id = domain.id,
            delta = domain.delta,
            reason = domain.reason,
            timestamp = domain.timestamp,
            habitId = domain.habitId,
            metadata = domain.metadata
        )
    }
}
