package com.example.restyoureyes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restyoureyes.data.BreakMetrics
import com.example.restyoureyes.data.SettingsRepository
import com.example.restyoureyes.data.StatsRepository
import com.example.restyoureyes.service.EyeRestService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {
    val isServiceRunning: StateFlow<Boolean> = EyeRestService.isServiceRunning
    val workDuration = settingsRepository.workDurationFlow.stateIn(viewModelScope, SharingStarted.Lazily, 20)
    val breakDuration = settingsRepository.breakDurationFlow.stateIn(viewModelScope, SharingStarted.Lazily, 20)
    val vibrationEnabled = settingsRepository.vibrationEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val autoDismiss = settingsRepository.autoDismissFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)

    val weeklyMetrics: StateFlow<BreakMetrics> = statsRepository.weeklyMetricsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreakMetrics())

    val historicalMetrics: StateFlow<BreakMetrics> = statsRepository.historicalMetricsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreakMetrics())

    fun updateWorkDuration(min: Int) = viewModelScope.launch { settingsRepository.saveWorkDuration(min) }
    fun updateBreakDuration(sec: Int) = viewModelScope.launch { settingsRepository.saveBreakDuration(sec) }
    fun updateVibration(enabled: Boolean) = viewModelScope.launch { settingsRepository.saveVibrationEnabled(enabled) }
    fun updateAutoDismiss(enabled: Boolean) = viewModelScope.launch { settingsRepository.saveAutoDismiss(enabled) }

    fun resetStats() = viewModelScope.launch { statsRepository.clearAllStats() }
}
