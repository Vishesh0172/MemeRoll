package com.example.memeroll.data

import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject


interface StorageRepository{
    suspend fun addImage(uri: Uri, userId: String): String?
    suspend fun deleteImage(id: Int)
}
class StorageRepositoryImpl @Inject constructor(
    storage: Storage
) : StorageRepository {

    private val bucket = storage.from("meme-images")

    override suspend fun addImage(uri: Uri, userId: String): String? {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatedDate = formatter.format(date)

        Log.d("Current TIme", formatedDate)

        val customPath = "public/$userId/${uri.pathSegments.last()}-$formatedDate.jpg"
        return try {
            bucket.upload(path = customPath, uri = uri){
            }

            return bucket.publicUrl(customPath)
        }catch (e: Exception){
            Log.d("StorageRepository", "Couldn't add to storage: $e")
            null
        }

    }

    override suspend fun deleteImage(id: Int) {
        TODO("Not yet implemented")
    }

}