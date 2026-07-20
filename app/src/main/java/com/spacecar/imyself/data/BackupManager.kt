package com.spacecar.imyself.data

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class BackupManager(
    private val context: Context,
    private val repository: TrackingRepository
) {
    private val gson = Gson()

    suspend fun createBackup(): BackupData {
        return withContext(Dispatchers.IO) {
            val dailyLogs = repository.getAllDailyLogsDirectly()
            val personalLogs = repository.getAllPersonalLogsDirectly()
            val sharedPrefs = context.getSharedPreferences("tracking_prefs", Context.MODE_PRIVATE)
            val startTime = sharedPrefs.getLong("start_time", System.currentTimeMillis())
            val firstTrackedDate = sharedPrefs.getLong("first_tracked_date", startTime)
            
            BackupData(dailyLogs, personalLogs, startTime, firstTrackedDate)
        }
    }

    suspend fun restoreBackup(backupData: BackupData) {
        withContext(Dispatchers.IO) {
            repository.restoreAllData(backupData.dailyLogs, backupData.personalLogs)
            val sharedPrefs = context.getSharedPreferences("tracking_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit()
                .putLong("start_time", backupData.startTimeMillis)
                .putLong("first_tracked_date", backupData.firstTrackedDateMillis)
                .apply()
        }
    }

    suspend fun performAutoBackup() {
        val backupData = createBackup()
        val json = gson.toJson(backupData)
        withContext(Dispatchers.IO) {
            try {
                val backupDir = File(context.getExternalFilesDir(null), "backups")
                if (!backupDir.exists()) backupDir.mkdirs()
                val backupFile = File(backupDir, "auto_backup.json")
                FileWriter(backupFile).use { it.write(json) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    suspend fun exportToUri(uri: Uri) {
        val backupData = createBackup()
        val json = gson.toJson(backupData)
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(json)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun importFromUri(uri: Uri): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    InputStreamReader(inputStream).use { reader ->
                        val backupData = gson.fromJson(reader, BackupData::class.java)
                        if (backupData != null) {
                            restoreBackup(backupData)
                            true
                        } else false
                    }
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
