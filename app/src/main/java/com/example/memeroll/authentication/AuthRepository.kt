package com.example.memeroll.authentication

import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signUp(email: String, password: String, name: String): Boolean
    suspend fun isAuthenticated(): Flow<SessionStatus>
    suspend fun signOut()
    suspend fun getCurrentUser(): UserInfo?
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth,
) : AuthRepository{

    override suspend fun signIn(email: String, password: String): Boolean {

        return try {
            auth.signInWith(Email){
                this.email = email
                this.password = password
            }
            true
        }catch (e: Exception){
            Log.d("Authentication", "Sign In Exception: $e")
            false
        }

    }

    override suspend fun signUp(email: String, password: String, name: String): Boolean {

        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            true
        } catch (e: Exception) {
            Log.d("AuthRepository", "Couldn't Sign Up: $e")
            false
        }
    }

    override suspend fun isAuthenticated(): Flow<SessionStatus> {

        return auth.sessionStatus

    }

    override suspend fun signOut(){

        return try {
            auth.signOut()

        }catch (e: Exception){
            Log.d("AuthRepository", "Couldn't Sign Out: $e")
            Unit
        }
    }

    override suspend fun getCurrentUser(): UserInfo? {
        return auth.currentUserOrNull()
    }


}