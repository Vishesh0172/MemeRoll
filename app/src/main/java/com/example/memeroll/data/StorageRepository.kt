package com.example.memeroll.data

import android.net.Uri
import android.util.Log
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import java.text.SimpleDateFormat
import java.util.Calendar
import javax.inject.Inject


interface StorageRepository{
    suspend fun addImage(uri: Uri, userId: String): String?
    suspend fun deleteImage(userId: String, path: String): Boolean
}
class StorageRepositoryImpl @Inject constructor(
    storage: Storage
) : StorageRepository {

    private val bucket = storage.from("meme-images")
    val date = Calendar.getInstance().time
    val formatter = SimpleDateFormat.getDateTimeInstance()
    val formatedDate = formatter.format(date)


    override suspend fun addImage(uri: Uri, userId: String): String? {


        Log.d("Current TIme", formatedDate)

        val customPath = "public/$userId/${uri.pathSegments.last()}-$formatedDate.jpg"
        return try {
            bucket.upload(path = customPath, uri = uri)
            bucket.publicUrl(customPath)
        }catch (e: Exception){
            Log.d("StorageRepository", "Couldn't add to storage: $e")
            null
        }

    }

    override suspend fun deleteImage(userId: String, path: String): Boolean {

        return try {
            bucket.delete(path)
            Log.d("StorageRepository", "Successfully removed meme Image from Storage")
            true
        }catch (e: Exception){
            Log.d("StorageRepository ", "Couldn't remove from storage: $e")
            false
        }

    }


}