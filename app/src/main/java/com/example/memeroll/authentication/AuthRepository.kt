package com.example.memeroll.authentication

import android.util.Log
import com.example.memeroll.model.UserDTO
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
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
            Log.d("Authentication", "Sign In return true")
            true
        }catch (e: Exception){
            Log.d("Authentication", "Sign In Exception: $e")
            false
        }

    }

    override suspend fun signUp(email: String, password: String, name: String): Boolean {
        Log.d("Authentication", "Sign Up function called")
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            true
        } catch (e: Exception) {
            Log.d("Authentication", "Sign Up Exception: $e")
            false
        }
    }

    override suspend fun isAuthenticated(): Flow<SessionStatus> {

        return auth.sessionStatus
        /*
        return auth.sessionStatus.map{
            when(it) {
                is SessionStatus.Authenticated -> {
                    Log.d("Authentication", "Session status Authenticated")
                    true
                }

                else -> {
                    Log.d("Authentication", "$it")
                    false
                }
            }

        }
         */
    }

    override suspend fun signOut(){
        Log.d("Authentication", "signOut function called")
        return try {
            auth.signOut()

        }catch (e: Exception){
            Log.d("Authentication", "Sign Out Exception: $e")
            Unit
        }
    }

    override suspend fun getCurrentUser(): UserInfo? {
        return auth.currentUserOrNull()
    }


}