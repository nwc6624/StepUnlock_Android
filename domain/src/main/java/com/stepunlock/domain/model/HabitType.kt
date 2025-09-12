package com.stepunlock.domain.model

enum class HabitType(val displayName: String, val description: String) {
    STEPS("Steps", "Walking and physical activity"),
    POMODORO("Focus Sessions", "25-minute focused work sessions"),
    WATER("Hydration", "Drinking water throughout the day"),
    JOURNAL("Journaling", "Reflective writing and gratitude"),
    CUSTOM("Custom", "Custom habits you define")
}
