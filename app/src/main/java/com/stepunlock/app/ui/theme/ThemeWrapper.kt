package com.stepunlock.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.stepunlock.app.data.manager.ThemeManager
import com.stepunlock.app.data.model.ThemePreference

@Composable
fun ThemeWrapper(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeManager = remember { ThemeManager(context) }
    
    val themePreference by themeManager.themePreference.collectAsState(initial = ThemePreference.SYSTEM)
    val systemDarkTheme = isSystemInDarkTheme()
    
    val shouldUseDarkTheme = when (themePreference) {
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
        ThemePreference.SYSTEM -> systemDarkTheme
    }
    
    StepUnlockTheme(
        darkTheme = shouldUseDarkTheme,
        content = content
    )
}
