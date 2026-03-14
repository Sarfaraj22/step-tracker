package com.example.steptracker.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.model.AuthException
import com.example.steptracker.domain.use_case.auth.LoginWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmail: LoginWithEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun validateEmail() {
        val email = _uiState.value.email
        val error = when {
            email.isBlank() -> "This field is required"
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> "Please enter a valid email address"
            else -> null
        }
        _uiState.update { it.copy(emailError = error) }
    }

    fun validatePassword() {
        val error = if (_uiState.value.password.isBlank()) "This field is required" else null
        _uiState.update { it.copy(passwordError = error) }
    }

    fun onLoginClick() {
        if (!validateFields()) return
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = loginWithEmail(state.email.trim(), state.password)
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSuccess = true) } },
                onFailure = { e ->
                    when (e) {
                        is AuthException.UserNotFound -> _uiState.update {
                            it.copy(isLoading = false, emailError = e.message)
                        }
                        is AuthException.WrongPassword -> _uiState.update {
                            it.copy(isLoading = false, passwordError = e.message)
                        }
                        else -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Sign-in failed. Please try again."
                            )
                        }
                    }
                }
            )
        }
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value

        val emailError = when {
            state.email.isBlank() -> "This field is required"
            !Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches() -> "Please enter a valid email address"
            else -> null
        }

        val passwordError = if (state.password.isBlank()) "This field is required" else null

        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }

        return emailError == null && passwordError == null
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
