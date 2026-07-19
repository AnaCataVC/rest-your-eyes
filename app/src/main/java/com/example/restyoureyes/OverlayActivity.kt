package com.example.restyoureyes

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.restyoureyes.data.SettingsRepository
import com.example.restyoureyes.ui.screens.OverlayScreen
import com.example.restyoureyes.ui.theme.RestYourEyesTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OverlayActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val settingsRepository = SettingsRepository(applicationContext)

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
                    onDismiss = { finish() }
                )
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
