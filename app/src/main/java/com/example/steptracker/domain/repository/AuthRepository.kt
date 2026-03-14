package com.example.steptracker.domain.repository

import com.example.steptracker.domain.model.User

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun registerWithEmail(email: String, password: String, displayName: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUser(): User?
    fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}
