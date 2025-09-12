package com.stepunlock.domain.usecase.app

import com.stepunlock.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLockedAppsCountUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<Int> {
        return appRepository.getLockedAppsCount()
    }
}
