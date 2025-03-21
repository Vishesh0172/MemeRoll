package com.example.memeroll.data

import android.util.Log
import com.example.memeroll.model.MemeDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.selects.select
import javax.inject.Inject

interface FeedDatabaseRepository{
    suspend fun getFeedMemes(from: Long): List<MemeDTO>
    suspend fun getMemeById(id: Int): MemeDTO
    suspend fun likeMeme()
    suspend fun addMeme(meme: MemeDTO): Int?
}

class FeedDatabaseRepositoryImpl @Inject constructor(
    private val database: Postgrest
) : FeedDatabaseRepository{

    override suspend fun getFeedMemes(from: Long): List<MemeDTO> {
       return try {
           val list = database.from("feed_table").select(){
               range(from = from, to = from + 10)

           }.decodeList<MemeDTO>()
           Log.d("MemeList", list.toString())
           list
        }catch (e: Exception){
           Log.d("FeedRepository", "Exception: $e")
           emptyList<MemeDTO>()
        }
    }

    override suspend fun getMemeById(id: Int): MemeDTO {
        return database.from("feed_table").select{
            filter {
                eq("id", id)
            }
        }.decodeSingle<MemeDTO>()
    }

    override suspend fun likeMeme() {
       // TODO("Not yet implemented")
    }

    override suspend fun addMeme(meme: MemeDTO): Int? {
        return try {
            database.from("feed_table").insert(meme) {
                select(columns = Columns.list("id"))
            }.decodeSingle<Map<String, Int>>().values.first()
        }catch (e: Exception){
            Log.d("FeedRepository", "Couldn't Add to Feed: $e")
            null
        }
    }

}
