package com.stepunlock.domain.usecase.habit

import com.stepunlock.domain.model.HabitProgress
import com.stepunlock.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayHabitProgressUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<HabitProgress> {
        return habitRepository.getTodayHabitProgress()
    }
}
