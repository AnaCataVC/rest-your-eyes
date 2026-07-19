package com.example.restyoureyes.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScreenStateReceiver(private val onScreenStateChanged: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_SCREEN_ON) {
            onScreenStateChanged(true)
        } else if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            onScreenStateChanged(false)
        }
    }
}
