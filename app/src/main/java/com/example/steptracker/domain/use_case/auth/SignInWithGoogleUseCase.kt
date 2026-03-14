package com.example.steptracker.domain.use_case.auth

import com.example.steptracker.domain.model.User
import com.example.steptracker.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> =
        authRepository.signInWithGoogle(idToken)
}
