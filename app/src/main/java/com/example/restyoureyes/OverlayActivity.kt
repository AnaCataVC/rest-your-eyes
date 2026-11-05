package com.example.restyoureyes

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.restyoureyes.data.SettingsRepository
import com.example.restyoureyes.data.StatsRepository
import com.example.restyoureyes.ui.screens.OverlayScreen
import com.example.restyoureyes.ui.theme.RestYourEyesTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class OverlayActivity : ComponentActivity() {

    private lateinit var statsRepository: StatsRepository
    private val isOutcomeRecorded = AtomicBoolean(false)
    private var hasTimeExpired = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val settingsRepository = SettingsRepository(applicationContext)
        statsRepository = StatsRepository(applicationContext)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (hasTimeExpired) {
                    recordCompletedAndFinish()
                } else {
                    recordSkippedAndFinish()
                }
            }
        })

        lifecycleScope.launch {
            val vibrationEnabled = settingsRepository.vibrationEnabledFlow.first()
            if (vibrationEnabled) {
                vibrateDevice()
            }
        }

        setContent {
            RestYourEyesTheme {
                OverlayScreen(
                    settingsRepository = settingsRepository,
                    onTimeExpired = {
                        hasTimeExpired = true
                    },
                    onCompleted = {
                        recordCompletedAndFinish()
                    },
                    onSkipped = {
                        recordSkippedAndFinish()
                    }
                )
            }
        }
    }

    private fun recordCompletedAndFinish() {
        if (isOutcomeRecorded.compareAndSet(false, true)) {
            lifecycleScope.launch {
                statsRepository.recordBreakCompleted()
                finish()
            }
        } else {
            finish()
        }
    }

    private fun recordSkippedAndFinish() {
        if (isOutcomeRecorded.compareAndSet(false, true)) {
            lifecycleScope.launch {
                statsRepository.recordBreakSkipped()
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isOutcomeRecorded.compareAndSet(false, true)) {
            lifecycleScope.launch {
                if (hasTimeExpired) {
                    statsRepository.recordBreakCompleted()
                } else {
                    statsRepository.recordBreakSkipped()
                }
            }
        }
    }

    private fun vibrateDevice() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }
}
