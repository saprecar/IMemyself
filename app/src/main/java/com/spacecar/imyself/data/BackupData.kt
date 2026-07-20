package com.spacecar.imyself.data

data class BackupData(
    val dailyLogs: List<DailyLog>,
    val personalLogs: List<PersonalLog>,
    val startTimeMillis: Long,
    val firstTrackedDateMillis: Long
)
