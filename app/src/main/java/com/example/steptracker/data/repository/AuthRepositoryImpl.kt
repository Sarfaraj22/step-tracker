package com.example.steptracker.data.repository

import com.example.steptracker.domain.model.AuthException
import com.example.steptracker.domain.model.User
import com.example.steptracker.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(AuthException.Unknown("Sign-in succeeded but user is null"))
            Result.success(user.toDomain())
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(AuthException.UserNotFound())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            val mapped = when (e.errorCode) {
                "ERROR_WRONG_PASSWORD" -> AuthException.WrongPassword()
                else -> AuthException.InvalidCredential()
            }
            Result.failure(mapped)
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError())
        } catch (e: Exception) {
            Result.failure(AuthException.Unknown(e.message ?: "Sign-in failed. Please try again."))
        }
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: return Result.failure(AuthException.Unknown("Registration succeeded but user is null"))
            val profileUpdate = userProfileChangeRequest { this.displayName = displayName }
            firebaseUser.updateProfile(profileUpdate).await()
            Result.success(firebaseUser.toDomain())
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(AuthException.EmailAlreadyInUse())
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError())
        } catch (e: Exception) {
            Result.failure(AuthException.Unknown(e.message ?: "Registration failed. Please try again."))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user
                ?: return Result.failure(AuthException.Unknown("Google sign-in succeeded but user is null"))
            Result.success(user.toDomain())
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError())
        } catch (e: Exception) {
            Result.failure(AuthException.Unknown(e.message ?: "Google sign-in failed. Please try again."))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> =
        runCatching {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }

    override fun getCurrentUser(): User? = firebaseAuth.currentUser?.toDomain()

    override fun signOut() = firebaseAuth.signOut()

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
                ?: return Result.failure(AuthException.Unknown("No authenticated user to delete"))
            Result.success(Unit)
        } catch (e: FirebaseAuthRecentLoginRequiredException) {
            Result.failure(AuthException.RecentLoginRequired())
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthException.NetworkError())
        } catch (e: Exception) {
            Result.failure(AuthException.Unknown(e.message ?: "Account deletion failed. Please try again."))
        }
    }

    private fun FirebaseUser.toDomain() = User(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName
    )
}
