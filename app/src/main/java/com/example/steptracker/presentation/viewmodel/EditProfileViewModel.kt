package com.example.steptracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steptracker.domain.use_case.auth.GetCurrentUserUseCase
import com.example.steptracker.domain.use_case.auth.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        val user = getCurrentUserUseCase()
        val displayName = user?.displayName.orEmpty()
        val spaceIndex = displayName.indexOf(' ')
        val firstName = if (spaceIndex >= 0) displayName.substring(0, spaceIndex) else displayName
        val lastName = if (spaceIndex >= 0) displayName.substring(spaceIndex + 1) else ""
        _uiState.update {
            it.copy(
                firstName = firstName,
                lastName = lastName,
                email = user?.email.orEmpty(),
            )
        }
    }

    fun onFirstNameChange(value: String) {
        _uiState.update { it.copy(firstName = value, errorMessage = null) }
    }

    fun onLastNameChange(value: String) {
        _uiState.update { it.copy(lastName = value, errorMessage = null) }
    }

    fun saveChanges() {
        val state = _uiState.value
        val displayName = buildString {
            append(state.firstName.trim())
            if (state.lastName.trim().isNotEmpty()) {
                append(" ")
                append(state.lastName.trim())
            }
        }
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            updateProfileUseCase(displayName)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = e.message ?: "Update failed. Please try again.")
                    }
                }
        }
    }
}
