package com.stepunlock.core.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object TimeUtils {
    
    /**
     * Get current timestamp in milliseconds (UTC)
     */
    fun nowMillis(): Long = System.currentTimeMillis()
    
    /**
     * Get start of today in milliseconds (UTC)
     */
    fun startOfTodayMillis(): Long {
        return LocalDate.now()
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }
    
    /**
     * Get start of day for given timestamp in milliseconds (UTC)
     */
    fun startOfDayMillis(timestamp: Long): Long {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }
    
    /**
     * Get end of day for given timestamp in milliseconds (UTC)
     */
    fun endOfDayMillis(timestamp: Long): Long {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .atTime(23, 59, 59, 999_999_999)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }
    
    /**
     * Check if two timestamps are on the same day
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val date1 = Instant.ofEpochMilli(timestamp1).atZone(ZoneOffset.UTC).toLocalDate()
        val date2 = Instant.ofEpochMilli(timestamp2).atZone(ZoneOffset.UTC).toLocalDate()
        return date1 == date2
    }
    
    /**
     * Get days between two timestamps
     */
    fun daysBetween(timestamp1: Long, timestamp2: Long): Long {
        val date1 = Instant.ofEpochMilli(timestamp1).atZone(ZoneOffset.UTC).toLocalDate()
        val date2 = Instant.ofEpochMilli(timestamp2).atZone(ZoneOffset.UTC).toLocalDate()
        return java.time.temporal.ChronoUnit.DAYS.between(date1, date2)
    }
    
    /**
     * Format duration in milliseconds to human readable string
     */
    fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }
    
    /**
     * Format timestamp to readable date string
     */
    fun formatDate(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    }
    
    /**
     * Format timestamp to readable time string
     */
    fun formatTime(timestamp: Long): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneOffset.UTC)
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    
    /**
     * Convert minutes to milliseconds
     */
    fun minutesToMillis(minutes: Int): Long = TimeUnit.MINUTES.toMillis(minutes.toLong())
    
    /**
     * Convert milliseconds to minutes
     */
    fun millisToMinutes(millis: Long): Int = TimeUnit.MILLISECONDS.toMinutes(millis).toInt()
}
