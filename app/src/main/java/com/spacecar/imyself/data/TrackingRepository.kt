package com.spacecar.imyself.data

import kotlinx.coroutines.flow.Flow

class TrackingRepository(private val trackingDao: TrackingDao) {
    val allLogs: Flow<List<DailyLog>> = trackingDao.getAllLogs()
    val allMilestones: Flow<List<StreakMilestone>> = trackingDao.getAllMilestones()

    suspend fun getLogByDate(date: String): DailyLog? {
        return trackingDao.getLogByDate(date)
    }

    suspend fun insertLog(log: DailyLog) {
        trackingDao.insertLog(log)
    }

    suspend fun deleteLogByDate(date: String) {
        trackingDao.deleteLogByDate(date)
    }

    suspend fun getLatestFellLog(): DailyLog? {
        return trackingDao.getLatestFellLog()
    }

    suspend fun getOldestLog(): DailyLog? {
        return trackingDao.getOldestLog()
    }

    suspend fun getActiveMilestone(): StreakMilestone? {
        return trackingDao.getActiveMilestone()
    }

    suspend fun insertMilestone(milestone: StreakMilestone) {
        trackingDao.insertMilestone(milestone)
    }
    
    suspend fun clearUnlockedMilestones() {
        trackingDao.clearUnlockedMilestones()
    }

    val allPersonalLogs: Flow<List<PersonalLog>> = trackingDao.getAllPersonalLogs()

    suspend fun insertPersonalLog(log: PersonalLog) {
        trackingDao.insertPersonalLog(log)
    }

    suspend fun updatePersonalLog(log: PersonalLog) {
        trackingDao.updatePersonalLog(log)
    }

    suspend fun deletePersonalLog(log: PersonalLog) {
        trackingDao.deletePersonalLog(log)
    }

    suspend fun getAllDailyLogsDirectly(): List<DailyLog> {
        return trackingDao.getAllLogsDirectly()
    }

    suspend fun getAllPersonalLogsDirectly(): List<PersonalLog> {
        return trackingDao.getAllPersonalLogsDirectly()
    }

    suspend fun restoreAllData(dailyLogs: List<DailyLog>, personalLogs: List<PersonalLog>) {
        trackingDao.clearAllDailyLogs()
        trackingDao.clearAllPersonalLogs()
        trackingDao.insertAllDailyLogs(dailyLogs)
        trackingDao.insertAllPersonalLogs(personalLogs)
    }
}
