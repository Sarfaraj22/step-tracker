package com.example.steptracker.domain.use_case.auth

import com.example.steptracker.domain.model.User
import com.example.steptracker.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}
