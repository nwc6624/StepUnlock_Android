package com.stepunlock.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            json.decodeFromString<List<String>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    @TypeConverter
    fun fromIntList(value: List<Int>): String {
        return json.encodeToString(value)
    }
    
    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return try {
            json.decodeFromString<List<Int>>(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
