package com.spacecar.imyself.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_logs")
data class PersonalLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val moodEmoji: String,
    val note: String,
    val isFellLog: Boolean
)
