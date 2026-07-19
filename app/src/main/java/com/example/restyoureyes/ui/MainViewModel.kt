package com.example.restyoureyes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restyoureyes.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {
    val workDuration = settingsRepository.workDurationFlow.stateIn(viewModelScope, SharingStarted.Lazily, 20)
    val breakDuration = settingsRepository.breakDurationFlow.stateIn(viewModelScope, SharingStarted.Lazily, 20)
    val vibrationEnabled = settingsRepository.vibrationEnabledFlow.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val autoDismiss = settingsRepository.autoDismissFlow.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun updateWorkDuration(min: Int) = viewModelScope.launch { settingsRepository.saveWorkDuration(min) }
    fun updateBreakDuration(sec: Int) = viewModelScope.launch { settingsRepository.saveBreakDuration(sec) }
    fun updateVibration(enabled: Boolean) = viewModelScope.launch { settingsRepository.saveVibrationEnabled(enabled) }
    fun updateAutoDismiss(enabled: Boolean) = viewModelScope.launch { settingsRepository.saveAutoDismiss(enabled) }
}
