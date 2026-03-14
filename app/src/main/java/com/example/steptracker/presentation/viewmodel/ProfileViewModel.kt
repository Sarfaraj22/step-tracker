package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.model.AuthException
import com.example.steptracker.domain.use_case.auth.DeleteAccountUseCase
import com.example.steptracker.domain.use_case.auth.GetCurrentUserUseCase
import com.example.steptracker.domain.use_case.auth.SignOutUseCase
import com.example.steptracker.domain.use_case.steps.ResetUserStepDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val stepsDistanceEnabled: Boolean = true,
    val dailyReminderEnabled: Boolean = true,
    val goalReachedNotificationEnabled: Boolean = true,
    val inactivityNudgeEnabled: Boolean = false,
    val biometricAuthEnabled: Boolean = false,
    val locationTrackingEnabled: Boolean = true,
    val isSignedOut: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val isAccountDeleted: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val deleteAccountError: String? = null,
    val isResettingData: Boolean = false,
    val resetDataMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val resetUserStepDataUseCase: ResetUserStepDataUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refreshUser()
    }

    fun refreshUser() {
        val user = getCurrentUserUseCase()
        _uiState.update {
            it.copy(
                userName = user?.displayName.orEmpty(),
                userEmail = user?.email.orEmpty(),
            )
        }
    }

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

    fun syncLocationPermission(granted: Boolean) {
        _uiState.update { it.copy(locationTrackingEnabled = granted) }
    }

    fun signOut() {
        signOutUseCase()
        _uiState.update { it.copy(isSignedOut = true) }
    }

    fun showDeleteAccountDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun dismissDeleteAccountDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun deleteAccount() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false, isDeletingAccount = true, deleteAccountError = null) }
        viewModelScope.launch {
            deleteAccountUseCase()
                .onSuccess {
                    _uiState.update { it.copy(isDeletingAccount = false, isAccountDeleted = true) }
                }
                .onFailure { e ->
                    if (e is AuthException.NetworkError) {
                        _uiState.update {
                            it.copy(isDeletingAccount = false, deleteAccountError = e.message)
                        }
                    } else {
                        // For any other failure (session expired, re-auth required, no current user, etc.)
                        // sign out locally and redirect to login.
                        signOutUseCase()
                        _uiState.update { it.copy(isDeletingAccount = false, isAccountDeleted = true) }
                    }
                }
        }
    }

    fun clearDeleteAccountError() {
        _uiState.update { it.copy(deleteAccountError = null) }
    }

    fun resetAllData() {
        _uiState.update {
            it.copy(
                isResettingData = true,
                resetDataMessage = null,
            )
        }
        viewModelScope.launch {
            resetUserStepDataUseCase()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isResettingData = false,
                            resetDataMessage = "All activity data has been reset.",
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isResettingData = false,
                            resetDataMessage = e.message ?: "Failed to reset data. Please try again.",
                        )
                    }
                }
        }
    }

    fun clearResetDataMessage() {
        _uiState.update { it.copy(resetDataMessage = null) }
    }
}
