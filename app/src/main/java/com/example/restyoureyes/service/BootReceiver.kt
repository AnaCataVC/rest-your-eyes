package com.example.restyoureyes.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // ACTION_BOOT_COMPLETED is for normal device startup.
        // QUICKBOOT_POWERON ensures the service restarts on devices with fast boot enabled.
        // ACTION_MY_PACKAGE_REPLACED ensures the service restarts automatically after the app is updated.
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON" || 
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            
            val serviceIntent = Intent(context, EyeRestService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
