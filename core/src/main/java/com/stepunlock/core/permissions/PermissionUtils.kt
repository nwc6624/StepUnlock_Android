package com.stepunlock.core.permissions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.app.AppOpsManager
import android.os.Build

object PermissionUtils {
    
    /**
     * Check if Usage Access permission is granted
     */
    fun hasUsageAccessPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            mode == AppOpsManager.MODE_ALLOWED
        } else {
            false
        }
    }
    
    /**
     * Check if System Alert Window permission is granted
     */
    fun hasSystemAlertWindowPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    
    /**
     * Check if Accessibility Service permission is granted
     */
    fun hasAccessibilityServicePermission(context: Context): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            0
        }
        
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                return settingValue.contains("${context.packageName}/${context.packageName}.services.StepUnlockAccessibilityService")
            }
        }
        return false
    }
    
    /**
     * Open Usage Access settings
     */
    fun openUsageAccessSettings(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        context.startActivity(intent)
    }
    
    /**
     * Open System Alert Window settings
     */
    fun openSystemAlertWindowSettings(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:${context.packageName}")
        context.startActivity(intent)
    }
    
    /**
     * Open Accessibility settings
     */
    fun openAccessibilitySettings(context: Context) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun hasAllRequiredPermissions(context: Context): Boolean {
        return hasUsageAccessPermission(context) || hasSystemAlertWindowPermission(context)
    }
    
    /**
     * Get missing permissions as a list of PermissionType
     */
    fun getMissingPermissions(context: Context): List<PermissionType> {
        val missing = mutableListOf<PermissionType>()
        
        if (!hasUsageAccessPermission(context)) {
            missing.add(PermissionType.USAGE_ACCESS)
        }
        
        if (!hasSystemAlertWindowPermission(context)) {
            missing.add(PermissionType.SYSTEM_ALERT_WINDOW)
        }
        
        return missing
    }
}

enum class PermissionType {
    USAGE_ACCESS,
    SYSTEM_ALERT_WINDOW,
    ACCESSIBILITY_SERVICE
}
