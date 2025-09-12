package com.stepunlock.domain.usecase

import com.stepunlock.core.Result
import com.stepunlock.domain.model.CreditTransaction
import com.stepunlock.domain.model.Session
import com.stepunlock.domain.repository.CreditRepository
import com.stepunlock.domain.repository.SessionRepository
import javax.inject.Inject

class SpendCreditsUseCase @Inject constructor(
    private val creditRepository: CreditRepository,
    private val sessionRepository: SessionRepository
) {
    
    suspend operator fun invoke(
        packageName: String,
        appName: String,
        creditsToSpend: Int,
        minutes: Int
    ): Result<Unit> {
        return try {
            // Check if user has enough credits
            val totalCreditsResult = creditRepository.getTotalCredits()
            if (totalCreditsResult.isError) {
                return Result.Error(totalCreditsResult.exceptionOrNull() ?: Exception("Failed to get credit balance"))
            }
            
            val totalCredits = totalCreditsResult.getOrNull() ?: 0
            if (totalCredits < creditsToSpend) {
                return Result.Error(Exception("Insufficient credits"))
            }
            
            // Record the credit spending transaction
            val spendingTransaction = CreditTransaction(
                delta = -creditsToSpend,
                reason = "unlock:$packageName",
                metadata = "minutes:$minutes"
            )
            
            val insertTransactionResult = creditRepository.insertTransaction(spendingTransaction)
            if (insertTransactionResult.isError) {
                return Result.Error(insertTransactionResult.exceptionOrNull() ?: Exception("Failed to record transaction"))
            }
            
            // Create unlock session
            val session = Session(
                packageName = packageName,
                appName = appName,
                startTime = System.currentTimeMillis(),
                grantedMinutes = minutes,
                creditsSpent = creditsToSpend,
                isActive = true
            )
            
            val insertSessionResult = sessionRepository.insertSession(session)
            if (insertSessionResult.isError) {
                return Result.Error(insertSessionResult.exceptionOrNull() ?: Exception("Failed to create session"))
            }
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
