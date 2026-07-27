package com.example.restyoureyes.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.restyoureyes.OverlayActivity
import com.example.restyoureyes.R
import com.example.restyoureyes.data.SettingsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class EyeRestService : Service() {

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
        
        createNotificationChannel()
        startForeground(1, createNotification(20)) // Default initial value

        serviceScope.launch {
            settingsRepository.breakDurationFlow.collect { seconds ->
                val notification = createNotification(seconds)
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(1, notification)
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
            val workDurationMin = settingsRepository.workDurationFlow.first()
            val targetTimeMs = workDurationMin * 60 * 1000L
            
            while (isActive) {
                val currentRemainingMs = targetTimeMs - accumulatedScreenTimeMs
                if (currentRemainingMs <= 0) {
                    showOverlay()
                    accumulatedScreenTimeMs = 0L
                    screenOnTimestamp = System.currentTimeMillis()
                } else {
                    delay(currentRemainingMs)
                    // If delay finishes without cancellation, time is up
                    showOverlay()
                    accumulatedScreenTimeMs = 0L
                    screenOnTimestamp = System.currentTimeMillis()
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
        unregisterReceiver(screenStateReceiver)
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "eye_rest_channel",
                "Eye Rest Tracker",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Rastrea el tiempo activo de pantalla"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(seconds: Int): Notification {
        return NotificationCompat.Builder(this, "eye_rest_channel")
            .setContentTitle("Rest Your Eyes")
            .setContentText("Descansa la vista, mira a otra parte por $seconds segundos")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
