package com.example.steptracker.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.model.AuthException
import com.example.steptracker.domain.use_case.auth.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmail: RegisterWithEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value, firstNameError = null) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value, lastNameError = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPasswordVisibility() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun validateFirstName() {
        val error = if (_uiState.value.firstName.isBlank()) "This field is required" else null
        _uiState.update { it.copy(firstNameError = error) }
    }

    fun validateLastName() {
        val error = if (_uiState.value.lastName.isBlank()) "This field is required" else null
        _uiState.update { it.copy(lastNameError = error) }
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
        val password = _uiState.value.password
        val error = when {
            password.isBlank() -> "This field is required"
            password.length < 8 -> "Password must be at least 8 characters"
            !password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !password.any { it.isDigit() } -> "Password must contain at least one number"
            !password.any { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
            else -> null
        }
        _uiState.update { it.copy(passwordError = error) }
    }

    fun validateConfirmPassword() {
        val state = _uiState.value
        val error = when {
            state.confirmPassword.isBlank() -> "This field is required"
            state.confirmPassword != state.password -> "Passwords do not match"
            else -> null
        }
        _uiState.update { it.copy(confirmPasswordError = error) }
    }

    fun onRegisterClick() {
        if (!validateFields()) return
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val displayName = "${state.firstName.trim()} ${state.lastName.trim()}"
            val result = registerWithEmail(state.email.trim(), state.password, displayName)
            result.fold(
                onSuccess = { _uiState.update { it.copy(isLoading = false, isSuccess = true) } },
                onFailure = { e ->
                    when (e) {
                        is AuthException.EmailAlreadyInUse -> _uiState.update {
                            it.copy(isLoading = false, emailError = e.message)
                        }
                        else -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Registration failed. Please try again."
                            )
                        }
                    }
                }
            )
        }
    }

    private fun validateFields(): Boolean {
        val state = _uiState.value
        var isValid = true

        val firstNameError = if (state.firstName.isBlank()) "This field is required" else null
        val lastNameError = if (state.lastName.isBlank()) "This field is required" else null

        val emailError = when {
            state.email.isBlank() -> "This field is required"
            !Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches() -> "Please enter a valid email address"
            else -> null
        }

        val passwordError = when {
            state.password.isBlank() -> "This field is required"
            state.password.length < 8 -> "Password must be at least 8 characters"
            !state.password.any { it.isUpperCase() } -> "Password must contain at least one uppercase letter"
            !state.password.any { it.isLowerCase() } -> "Password must contain at least one lowercase letter"
            !state.password.any { it.isDigit() } -> "Password must contain at least one number"
            !state.password.any { !it.isLetterOrDigit() } -> "Password must contain at least one special character"
            else -> null
        }

        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "This field is required"
            state.confirmPassword != state.password -> "Passwords do not match"
            else -> null
        }

        if (firstNameError != null || lastNameError != null || emailError != null ||
            passwordError != null || confirmPasswordError != null
        ) {
            isValid = false
        }

        _uiState.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        }

        return isValid
    }

    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
