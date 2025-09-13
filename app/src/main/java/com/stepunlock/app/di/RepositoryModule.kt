package com.stepunlock.app.di

import com.stepunlock.app.data.repository.AppRepository
import com.stepunlock.app.data.repository.AppRepositoryImpl
import com.stepunlock.app.data.repository.CreditRepository
import com.stepunlock.app.data.repository.CreditRepositoryImpl
import com.stepunlock.app.data.repository.HabitRepository
import com.stepunlock.app.data.repository.HabitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository
    
    @Binds
    @Singleton
    abstract fun bindCreditRepository(impl: CreditRepositoryImpl): CreditRepository
    
    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository
}
