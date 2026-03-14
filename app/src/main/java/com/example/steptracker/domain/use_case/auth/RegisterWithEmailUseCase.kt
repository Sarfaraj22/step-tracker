package com.example.steptracker.domain.use_case.auth

import com.example.steptracker.domain.model.User
import com.example.steptracker.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = authRepository.registerWithEmail(email, password, displayName)
}
