package com.stepunlock.domain.usecase

import com.stepunlock.core.Result
import com.stepunlock.domain.repository.AppRuleRepository
import com.stepunlock.domain.repository.SessionRepository
import javax.inject.Inject

class IsAppLockedUseCase @Inject constructor(
    private val appRuleRepository: AppRuleRepository,
    private val sessionRepository: SessionRepository
) {
    
    suspend operator fun invoke(packageName: String): Result<Boolean> {
        return try {
            // Check if app is configured as locked
            val appRuleResult = appRuleRepository.getAppRule(packageName)
            if (appRuleResult.isError) {
                return Result.Error(appRuleResult.exceptionOrNull() ?: Exception("Failed to get app rule"))
            }
            
            val appRule = appRuleResult.getOrNull()
            if (appRule?.locked != true) {
                return Result.Success(false)
            }
            
            // Check if there's an active session
            val sessionResult = sessionRepository.getActiveSessionForApp(packageName)
            if (sessionResult.isError) {
                return Result.Error(sessionResult.exceptionOrNull() ?: Exception("Failed to get session"))
            }
            
            val activeSession = sessionResult.getOrNull()
            if (activeSession != null && !activeSession.isExpired) {
                // App has active session, not locked
                Result.Success(false)
            } else {
                // App is locked
                Result.Success(true)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
