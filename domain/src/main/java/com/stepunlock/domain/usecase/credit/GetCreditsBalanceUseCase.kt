package com.stepunlock.domain.usecase.credit

import com.stepunlock.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCreditsBalanceUseCase @Inject constructor(
    private val creditRepository: CreditRepository
) {
    operator fun invoke(): Flow<Int> {
        return creditRepository.getCreditsBalance()
    }
}
