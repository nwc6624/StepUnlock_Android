package com.stepunlock.app.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.stepunlock.app.data.entity.AppRuleEntity

object AppDetectionUtils {
    
    data class InstalledApp(
        val packageName: String,
        val appName: String,
        val icon: Drawable?,
        val category: String,
        val isSystemApp: Boolean
    )
    
    fun getInstalledApps(context: Context): List<InstalledApp> {
        val packageManager = context.packageManager
        val installedApps = mutableListOf<InstalledApp>()
        
        try {
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            
            for (packageInfo in packages) {
                // Skip system apps and our own app
                if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 || 
                    packageInfo.packageName == context.packageName) {
                    continue
                }
                
                val appName = packageManager.getApplicationLabel(packageInfo).toString()
                val icon = try {
                    packageManager.getApplicationIcon(packageInfo.packageName)
                } catch (e: Exception) {
                    null
                }
                
                val category = getAppCategory(packageInfo.packageName, appName)
                
                installedApps.add(
                    InstalledApp(
                        packageName = packageInfo.packageName,
                        appName = appName,
                        icon = icon,
                        category = category,
                        isSystemApp = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return installedApps.sortedBy { it.appName }
    }
    
    private fun getAppCategory(packageName: String, appName: String): String {
        return when {
            // Social Media
            packageName.contains("facebook") || packageName.contains("twitter") || 
            packageName.contains("instagram") || packageName.contains("snapchat") ||
            packageName.contains("tiktok") || packageName.contains("whatsapp") ||
            packageName.contains("telegram") || packageName.contains("discord") ||
            packageName.contains("reddit") || packageName.contains("linkedin") -> "Social"
            
            // Entertainment
            packageName.contains("youtube") || packageName.contains("netflix") ||
            packageName.contains("spotify") || packageName.contains("twitch") ||
            packageName.contains("prime") || packageName.contains("hulu") ||
            packageName.contains("disney") -> "Entertainment"
            
            // Games
            packageName.contains("game") || appName.contains("Game") ||
            packageName.contains("unity") || packageName.contains("unreal") -> "Games"
            
            // Shopping
            packageName.contains("amazon") || packageName.contains("ebay") ||
            packageName.contains("shop") || packageName.contains("buy") -> "Shopping"
            
            // News
            packageName.contains("news") || packageName.contains("cnn") ||
            packageName.contains("bbc") || packageName.contains("reuters") -> "News"
            
            else -> "Other"
        }
    }
    
    fun convertToAppRuleEntities(installedApps: List<InstalledApp>): List<AppRuleEntity> {
        return installedApps.map { app ->
            AppRuleEntity(
                packageName = app.packageName,
                appName = app.appName,
                isLocked = true,
                unlockCost = getDefaultUnlockCost(app.category),
                unlockDuration = 15 * 60 * 1000, // 15 minutes
                isUnlocked = false,
                unlockedUntil = 0,
                iconUri = null, // TODO: Convert drawable to URI
                category = app.category,
                lastUsed = 0,
                usageCount = 0
            )
        }
    }
    
    private fun getDefaultUnlockCost(category: String): Int {
        return when (category) {
            "Social" -> 15
            "Entertainment" -> 20
            "Games" -> 25
            "Shopping" -> 10
            "News" -> 5
            else -> 10
        }
    }
}
