package com.example.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val message: String, val exception: Throwable? = null) : AuthResult<Nothing>
    data object Loading : AuthResult<Nothing>
}

/**
 * Repository providing boilerplate and implementation for Firebase Email/Password Authentication.
 */
class FirebaseAuthRepository(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            val app = try {
                if (FirebaseApp.getApps(context).isNotEmpty()) {
                    FirebaseApp.getInstance()
                } else {
                    FirebaseApp.initializeApp(context)
                }
            } catch (_: Exception) {
                null
            } ?: run {
                try {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:595260247651:android:com.aistudio.teenpatti.royal")
                        .setApiKey("AIzaSyB_LocalEnvironmentKeyTeenPatti")
                        .setProjectId("teenpatti-royal")
                        .build()
                    FirebaseApp.initializeApp(context, options)
                } catch (_: Exception) {
                    null
                }
            }

            if (app != null) {
                try {
                    FirebaseAuth.getInstance(app)
                } catch (e: Exception) {
                    Log.w(TAG, "FirebaseAuth instance unavailable: ${e.message}")
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth initialization fallback: ${e.message}")
            null
        }
    }

    val isFirebaseAvailable: Boolean
        get() = auth != null

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    val isUserSignedIn: Boolean
        get() = auth?.currentUser != null

    /**
     * Observes real-time Firebase Auth state changes (signed in, signed out, token refreshed).
     */
    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { currentAuth ->
            trySend(currentAuth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    /**
     * Sign in with email and password.
     */
    suspend fun signInWithEmail(email: String, pass: String): AuthResult<FirebaseUser> {
        val firebaseAuth = auth ?: return AuthResult.Error(
            "Firebase is not initialized. Please ensure google-services.json is present in the app directory."
        )

        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        if (cleanEmail.isEmpty() || cleanPass.isEmpty()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }

        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(cleanEmail, cleanPass).awaitTask()
            val user = result.user
            if (user != null) {
                Log.d(TAG, "Firebase sign-in successful: ${user.uid} (${user.email})")
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to obtain signed-in Firebase user.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase sign-in failed: ${e.message}")
            AuthResult.Error(getReadableAuthErrorMessage(e), e)
        }
    }

    /**
     * Register a new user with email and password, and optionally set their display name.
     */
    suspend fun signUpWithEmail(
        email: String,
        pass: String,
        displayName: String? = null
    ): AuthResult<FirebaseUser> {
        val firebaseAuth = auth ?: return AuthResult.Error(
            "Firebase is not initialized. Please ensure google-services.json is present in the app directory."
        )

        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        if (cleanEmail.isEmpty() || cleanPass.isEmpty()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }

        if (cleanPass.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }

        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, cleanPass).awaitTask()
            val user = result.user
            if (user != null) {
                if (!displayName.isNullOrBlank()) {
                    try {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.trim())
                            .build()
                        user.updateProfile(profileUpdates).awaitTask()
                    } catch (pe: Exception) {
                        Log.w(TAG, "Could not update user display name", pe)
                    }
                }
                Log.d(TAG, "Firebase registration successful: ${user.uid}")
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Failed to create new Firebase user.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase sign-up failed: ${e.message}")
            AuthResult.Error(getReadableAuthErrorMessage(e), e)
        }
    }

    /**
     * Sends a password reset link to the specified email address.
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult<Unit> {
        val firebaseAuth = auth ?: return AuthResult.Error(
            "Firebase is not initialized. Please ensure google-services.json is present in the app directory."
        )

        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty()) {
            return AuthResult.Error("Please provide an email address.")
        }

        return try {
            firebaseAuth.sendPasswordResetEmail(cleanEmail).awaitTask()
            Log.d(TAG, "Password reset email sent to $cleanEmail")
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Password reset failed: ${e.message}")
            AuthResult.Error(getReadableAuthErrorMessage(e), e)
        }
    }

    /**
     * Signs out the current Firebase user.
     */
    fun signOut() {
        try {
            auth?.signOut()
            Log.d(TAG, "Firebase user signed out successfully.")
        } catch (e: Exception) {
            Log.w(TAG, "Error during Firebase sign-out: ${e.message}")
        }
    }

    private fun getReadableAuthErrorMessage(e: Throwable): String {
        val msg = e.message.orEmpty()
        return when {
            msg.contains("API key", ignoreCase = true) || msg.contains("SERVICE_NOT_AVAILABLE", ignoreCase = true) ->
                "Firebase service not configured. Add google-services.json or use guest login."
            msg.contains("The email address is badly formatted", ignoreCase = true) ->
                "Invalid email format. Please check your email address."
            msg.contains("There is no user record", ignoreCase = true) ||
            msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
            msg.contains("password is invalid", ignoreCase = true) ->
                "Incorrect email or password."
            msg.contains("email address is already in use", ignoreCase = true) ->
                "An account with this email address already exists."
            msg.contains("network error", ignoreCase = true) ->
                "Network connection failed. Please check your internet connection."
            msg.contains("blocked all requests from this device", ignoreCase = true) ->
                "Too many failed attempts. Please try again later."
            else -> e.localizedMessage ?: "Authentication failed. Please try again."
        }
    }

    companion object {
        private const val TAG = "FirebaseAuthRepo"
    }
}

/**
 * Extension function to safely await Google Task completion as a suspending coroutine.
 */
suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        if (continuation.isActive) {
            continuation.resume(result)
        }
    }
    addOnFailureListener { exception ->
        if (continuation.isActive) {
            continuation.resumeWithException(exception)
        }
    }
    addOnCanceledListener {
        if (continuation.isActive) {
            continuation.cancel()
        }
    }
}
