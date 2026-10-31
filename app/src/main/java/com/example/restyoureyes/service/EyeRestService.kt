package com.example.restyoureyes.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.restyoureyes.MainActivity
import com.example.restyoureyes.OverlayActivity
import com.example.restyoureyes.R
import com.example.restyoureyes.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class EyeRestService : Service() {

    companion object {
        private const val CHANNEL_ID = "eye_rest_channel"
        private const val NOTIFICATION_ID = 1

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var screenStateReceiver: ScreenStateReceiver
    
    private var isScreenOn = true
    private var accumulatedScreenTimeMs = 0L
    private var screenOnTimestamp = 0L
    
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        settingsRepository = SettingsRepository(applicationContext)
        _isServiceRunning.value = true
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification(20, 20))

        serviceScope.launch {
            combine(
                settingsRepository.breakDurationFlow,
                settingsRepository.workDurationFlow
            ) { breakSec, workMin ->
                Pair(breakSec, workMin)
            }.collect { (breakSec, workMin) ->
                val notification = createNotification(breakSec, workMin)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            }
        }

        screenStateReceiver = ScreenStateReceiver { screenOn ->
            handleScreenStateChange(screenOn)
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, filter)
        
        screenOnTimestamp = System.currentTimeMillis()
        startTimer()
    }

    private fun handleScreenStateChange(screenOn: Boolean) {
        if (isScreenOn == screenOn) return
        isScreenOn = screenOn
        
        accumulatedScreenTimeMs = 0L
        if (screenOn) {
            screenOnTimestamp = System.currentTimeMillis()
            startTimer()
        } else {
            timerJob?.cancel()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            settingsRepository.workDurationFlow.collectLatest { workDurationMin ->
                val targetTimeMs = workDurationMin * 60 * 1000L
                
                while (isActive) {
                    val currentRemainingMs = targetTimeMs - accumulatedScreenTimeMs
                    if (currentRemainingMs <= 0) {
                        showOverlay()
                        accumulatedScreenTimeMs = 0L
                        screenOnTimestamp = System.currentTimeMillis()
                    } else {
                        delay(currentRemainingMs)
                        showOverlay()
                        accumulatedScreenTimeMs = 0L
                        screenOnTimestamp = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    private fun showOverlay() {
        val intent = Intent(this, OverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceRunning.value = false
        unregisterReceiver(screenStateReceiver)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(breakSec: Int, workMin: Int): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_active_format, breakSec, workMin))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
