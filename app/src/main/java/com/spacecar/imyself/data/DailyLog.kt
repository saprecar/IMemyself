package com.spacecar.imyself.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class LogState {
    GREEN,  // Target reached
    YELLOW, // On track
    ORANGE, // Fell once
    RED     // Fell continuously
}

@Entity(tableName = "daily_log")
data class DailyLog(
    @PrimaryKey val date: String, // Stored as ISO string YYYY-MM-DD
    val state: LogState,
    val notes: String? = null
)

@Entity(tableName = "streak_milestone")
data class StreakMilestone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetDay: Int,
    val isLocked: Boolean,
    val dateReached: String? = null
)
