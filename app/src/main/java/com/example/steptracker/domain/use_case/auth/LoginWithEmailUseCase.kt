package com.example.steptracker.domain.use_case.auth

import com.example.steptracker.domain.model.User
import com.example.steptracker.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> =
        authRepository.signInWithEmail(email, password)
}
