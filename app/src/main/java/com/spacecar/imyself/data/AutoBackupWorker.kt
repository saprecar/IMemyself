package com.spacecar.imyself.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AutoBackupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = TrackingRepository(database.trackingDao())
            val backupManager = BackupManager(applicationContext, repository)
            
            backupManager.performAutoBackup()
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
