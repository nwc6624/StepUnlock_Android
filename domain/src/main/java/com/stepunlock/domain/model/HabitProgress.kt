package com.stepunlock.domain.model

data class HabitProgress(
    val stepsToday: Int = 0,
    val stepsTarget: Int = 10000,
    val stepsProgress: Float = 0f,
    val pomodorosCompleted: Int = 0,
    val pomodorosTarget: Int = 4,
    val waterGlasses: Int = 0,
    val waterTarget: Int = 8,
    val journalEntries: Int = 0,
    val journalTarget: Int = 1,
    val customHabitsCompleted: Int = 0
) {
    val totalHabitsCompleted: Int
        get() = pomodorosCompleted + journalEntries + customHabitsCompleted
    
    val totalHabitsTarget: Int
        get() = pomodorosTarget + journalTarget
}
