package com.stepunlock.data.local

import com.stepunlock.domain.model.HabitConfig

object DefaultDataProvider {
    
    fun getDefaultHabitConfigs(): List<HabitConfig> {
        return listOf(
            HabitConfig(
                id = "steps",
                name = "Steps",
                enabled = true,
                earnRate = 2, // 2 credits per 1000 steps
                goalPerDay = 10000,
                unit = "steps",
                iconName = "directions_walk",
                color = "#FF9800"
            ),
            HabitConfig(
                id = "pomodoro",
                name = "Focus Sessions",
                enabled = true,
                earnRate = 6, // 6 credits per 25min session
                goalPerDay = 4,
                unit = "sessions",
                iconName = "timer",
                color = "#4CAF50"
            ),
            HabitConfig(
                id = "water",
                name = "Water",
                enabled = true,
                earnRate = 1, // 1 credit per 8oz glass
                goalPerDay = 8,
                unit = "glasses",
                iconName = "local_drink",
                color = "#2196F3"
            ),
            HabitConfig(
                id = "journal",
                name = "Journaling",
                enabled = true,
                earnRate = 3, // 3 credits per 5min session
                goalPerDay = 1,
                unit = "sessions",
                iconName = "edit",
                color = "#9C27B0"
            )
        )
    }
}
