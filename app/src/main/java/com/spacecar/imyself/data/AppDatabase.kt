package com.spacecar.imyself.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackingDao {
    @Query("SELECT * FROM daily_log ORDER BY date ASC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_log ORDER BY date ASC")
    suspend fun getAllLogsDirectly(): List<DailyLog>

    @Query("DELETE FROM daily_log")
    suspend fun clearAllDailyLogs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDailyLogs(logs: List<DailyLog>)

    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    suspend fun getLogByDate(date: String): DailyLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog)

    @Query("DELETE FROM daily_log WHERE date = :date")
    suspend fun deleteLogByDate(date: String)

    @Query("SELECT * FROM daily_log WHERE state IN ('ORANGE', 'RED') ORDER BY date DESC LIMIT 1")
    suspend fun getLatestFellLog(): DailyLog?

    @Query("SELECT * FROM daily_log ORDER BY date ASC LIMIT 1")
    suspend fun getOldestLog(): DailyLog?
    
    @Query("SELECT * FROM streak_milestone ORDER BY id ASC")
    fun getAllMilestones(): Flow<List<StreakMilestone>>
    
    @Query("SELECT * FROM streak_milestone WHERE isLocked = 0 ORDER BY id ASC LIMIT 1")
    suspend fun getActiveMilestone(): StreakMilestone?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: StreakMilestone)
    
    @Query("DELETE FROM streak_milestone WHERE isLocked = 0")
    suspend fun clearUnlockedMilestones()

    @Query("SELECT * FROM personal_logs ORDER BY timestamp DESC")
    fun getAllPersonalLogs(): Flow<List<PersonalLog>>

    @Query("SELECT * FROM personal_logs ORDER BY timestamp DESC")
    suspend fun getAllPersonalLogsDirectly(): List<PersonalLog>

    @Query("DELETE FROM personal_logs")
    suspend fun clearAllPersonalLogs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPersonalLogs(logs: List<PersonalLog>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalLog(log: PersonalLog)

    @Update
    suspend fun updatePersonalLog(log: PersonalLog)

    @Delete
    suspend fun deletePersonalLog(log: PersonalLog)
}

@Database(entities = [DailyLog::class, StreakMilestone::class, PersonalLog::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "imyself_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
