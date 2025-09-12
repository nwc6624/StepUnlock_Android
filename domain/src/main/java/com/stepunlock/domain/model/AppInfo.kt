package com.stepunlock.domain.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val category: String? = null,
    val isSystemApp: Boolean = false,
    val isLocked: Boolean = false
)
