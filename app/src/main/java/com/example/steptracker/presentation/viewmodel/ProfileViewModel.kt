package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.model.AuthException
import com.example.steptracker.domain.use_case.auth.DeleteAccountUseCase
import com.example.steptracker.domain.use_case.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val userName: String = "John Doe",
    val userEmail: String = "john.doe@email.com",
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
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {

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
}
