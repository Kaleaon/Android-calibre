package com.universalmedialibrary.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.universalmedialibrary.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CalibreImportForegroundService : Service() {

    @Inject
    lateinit var calibreImportService: CalibreImportService

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY
        
        val calibreDbPath = intent.getStringExtra(EXTRA_DB_PATH) ?: return START_NOT_STICKY
        val libraryRootPath = intent.getStringExtra(EXTRA_ROOT_PATH) ?: return START_NOT_STICKY
        val libraryId = intent.getLongExtra(EXTRA_LIBRARY_ID, -1)
        if (libraryId == -1L) return START_NOT_STICKY

        startForeground(NOTIFICATION_ID, createNotification("Importing Calibre library..."))

        serviceScope.launch {
            try {
                calibreImportService.importCalibreDatabase(calibreDbPath, libraryRootPath, libraryId)
                updateNotification("Import complete!", "Successfully imported your Calibre library.")
            } catch (e: Exception) {
                updateNotification("Import failed", e.message ?: "An unknown error occurred.")
            } finally {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    private fun createNotification(contentText: String): Notification {
        createNotificationChannel()
        // NOTE: This assumes a placeholder icon exists at R.drawable.ic_import_export
        // In a real scenario, this resource would need to be added.
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Library Import")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .build()
    }

    private fun updateNotification(title: String, contentText: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Calibre Import Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel all coroutines to prevent memory leaks and ensure proper cleanup
        serviceScope.cancel()
    }

    companion object {
        const val EXTRA_DB_PATH = "EXTRA_DB_PATH"
        const val EXTRA_ROOT_PATH = "EXTRA_ROOT_PATH"
        const val EXTRA_LIBRARY_ID = "EXTRA_LIBRARY_ID"
        const val CHANNEL_ID = "CalibreImportServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}
