package com.example.steptracker.data.repository

import com.example.steptracker.domain.model.User
import com.example.steptracker.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> =
        runCatching {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.toDomain() ?: error("Sign-in succeeded but user is null")
        }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = runCatching {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = result.user ?: error("Registration succeeded but user is null")
        val profileUpdate = userProfileChangeRequest { this.displayName = displayName }
        firebaseUser.updateProfile(profileUpdate).await()
        firebaseUser.toDomain()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }

    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toDomain()

    override fun signOut() = firebaseAuth.signOut()

    private fun FirebaseUser.toDomain() = User(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName
    )
}
