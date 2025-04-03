package com.example.memeroll.di

import com.example.memeroll.BuildConfig
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.StorageRepositoryImpl
import com.example.memeroll.data.UserDataRepositoryImpl
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

    private const val SUPABASE_KEY = BuildConfig.SUPABASE_ANON_KEY
    private const val SUPABASE_URL = BuildConfig.SUPABASE_URL

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient{

        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
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

    @Provides
    @Singleton
    fun provideSupabaseStorageRepository(storage: Storage): StorageRepositoryImpl {
        return StorageRepositoryImpl(storage)
    }

    @Provides
    @Singleton
    fun provideSupabaseUserRepository(database: Postgrest): UserDataRepositoryImpl {
        return UserDataRepositoryImpl(database)
    }




}