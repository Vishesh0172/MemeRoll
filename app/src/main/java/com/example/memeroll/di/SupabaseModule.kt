package com.example.memeroll.di

import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient{

        val supabase_key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJnY3JsZGZoc2xlYWJkeW53bWdnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE5NTA2MzQsImV4cCI6MjA1NzUyNjYzNH0.f1kW8Bn9wt4NYJbJhNXV5jAWdoJHK3HsPc2ujditCAE"
        val supabase_url = "https://bgcrldfhsleabdynwmgg.supabase.co"
        return createSupabaseClient(
            supabaseUrl = supabase_url,
            supabaseKey = supabase_key
        ){
            install(Postgrest)
            install(Auth){
                flowType = FlowType.PKCE
                scheme = "app"
                host = "supabase.com"
            }
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideSupabaseDatabase(client: SupabaseClient): Postgrest {
        return client.postgrest
    }

    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth {
        return client.auth
    }


    @Provides
    @Singleton
    fun provideSupabaseStorage(client: SupabaseClient): Storage {
        return client.storage
    }

    @Provides
    @Singleton
    fun provideSupabaseAuthRepository(auth: Auth): AuthRepositoryImpl {
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun provideSupabaseFeedRepository(postgrest: Postgrest): FeedDatabaseRepositoryImpl {
        return FeedDatabaseRepositoryImpl(postgrest)
    }



}