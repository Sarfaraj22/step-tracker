package com.example.steptracker.domain.model

sealed class AuthException(message: String) : Exception(message) {
    /** No account exists for the given email address. */
    class UserNotFound : AuthException("No account found with this email address.")

    /** An account already exists with this email address. */
    class EmailAlreadyInUse : AuthException("An account with this email already exists.")

    /** The password supplied is incorrect. */
    class WrongPassword : AuthException("Incorrect password. Please try again.")

    /**
     * Firebase unified credential error (BOM 32+). The SDK no longer distinguishes
     * between a missing user and a wrong password to prevent email enumeration.
     */
    class InvalidCredential : AuthException("Incorrect email or password. Please try again.")

    /**
     * Firebase requires the user to have signed in recently before deleting their account.
     * The caller should sign the user out and send them to login to re-authenticate.
     */
    class RecentLoginRequired : AuthException("For your security, please sign in again before deleting your account.")

    /** Device has no network connectivity. */
    class NetworkError : AuthException("No internet connection. Please check your network.")

    /** Catch-all for unexpected Firebase errors. */
    class Unknown(message: String) : AuthException(message)
}
