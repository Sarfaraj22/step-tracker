package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val userName: String = "John Doe",
    val userEmail: String = "john.doe@email.com",
    val stepsDistanceEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val goalReachedNotificationEnabled: Boolean = true,
    val inactivityNudgeEnabled: Boolean = false,
    val biometricAuthEnabled: Boolean = false,
    val locationTrackingEnabled: Boolean = true,
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun toggleStepsDistance(enabled: Boolean) {
        _uiState.update { it.copy(stepsDistanceEnabled = enabled) }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        _uiState.update { it.copy(dailyReminderEnabled = enabled) }
    }

    fun toggleGoalReachedNotification(enabled: Boolean) {
        _uiState.update { it.copy(goalReachedNotificationEnabled = enabled) }
    }

    fun toggleInactivityNudge(enabled: Boolean) {
        _uiState.update { it.copy(inactivityNudgeEnabled = enabled) }
    }

    fun toggleBiometricAuth(enabled: Boolean) {
        _uiState.update { it.copy(biometricAuthEnabled = enabled) }
    }

    fun toggleLocationTracking(enabled: Boolean) {
        _uiState.update { it.copy(locationTrackingEnabled = enabled) }
    }
}
