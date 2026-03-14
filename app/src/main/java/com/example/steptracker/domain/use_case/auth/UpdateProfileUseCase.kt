package com.example.steptracker.domain.use_case.auth

import com.example.steptracker.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(displayName: String): Result<Unit> =
        authRepository.updateProfile(displayName)
}
