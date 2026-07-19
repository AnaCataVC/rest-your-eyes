package com.example.restyoureyes.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val WORK_DURATION_MINUTES = intPreferencesKey("work_duration_minutes")
        val BREAK_DURATION_SECONDS = intPreferencesKey("break_duration_seconds")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val AUTO_DISMISS = booleanPreferencesKey("auto_dismiss")
    }

    val workDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[WORK_DURATION_MINUTES] ?: 20
    }

    val breakDurationFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[BREAK_DURATION_SECONDS] ?: 20
    }

    val vibrationEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[VIBRATION_ENABLED] ?: true
    }

    val autoDismissFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[AUTO_DISMISS] ?: false
    }

    suspend fun saveWorkDuration(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[WORK_DURATION_MINUTES] = minutes
        }
    }

    suspend fun saveBreakDuration(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[BREAK_DURATION_SECONDS] = seconds
        }
    }

    suspend fun saveVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun saveAutoDismiss(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_DISMISS] = enabled
        }
    }
}
