package com.example.memeroll.data

import android.util.Log
import com.example.memeroll.model.MemeDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FeedDatabaseRepository {
    suspend fun getLikesById(postId: Int): Double
    suspend fun getFeedMemes(from: Long): List<MemeDTO>
    suspend fun getMemeById(id: Int): MemeDTO
    suspend fun likeMeme(postId: Int)
    suspend fun unlikeMeme(postId: Int)
    suspend fun addMeme(meme: MemeDTO): Int?
    suspend fun getMemesByIds(idList: List<Int>): Flow<List<MemeDTO>>
}

    class FeedDatabaseRepositoryImpl @Inject constructor(
        private val database: Postgrest
    ) : FeedDatabaseRepository {
        override suspend fun getLikesById(postId: Int): Double {
            return getMemeById(postId).likeCount
        }

        override suspend fun getFeedMemes(from: Long): List<MemeDTO> {
            return try {
                val list = database.from("feed_table").select() {
                    range(from = from, to = from + 10)

                }.decodeList<MemeDTO>()
                Log.d("MemeList", list.toString())
                list
            } catch (e: Exception) {
                Log.d("FeedRepository", "Exception: $e")
                emptyList<MemeDTO>()
            }
        }

        override suspend fun getMemeById(id: Int): MemeDTO {
            return database.from("feed_table").select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle<MemeDTO>()
        }

        override suspend fun likeMeme(postId: Int) {

            Log.d("FeedRepository", "$postId")
            try {
                val currentLikes = getMemeById(postId).likeCount
                database.from("feed_table").update(
                    {
                        set("like_count", currentLikes.toLong() + 1L)
                    }
                ) {
                    filter {
                        eq("id", postId.toLong())
                    }
                }
            } catch (e: Exception) {
                Log.d("FeedRepository", "Couldn't Like Post: $e")
            }
        }

        override suspend fun unlikeMeme(postId: Int) {
            Log.d("FeedRepository", "$postId")
            try {
                val currentLikes = getMemeById(postId).likeCount
                database.from("feed_table").update(
                    {
                        set("like_count", currentLikes.toLong() - 1L)
                    }
                ) {
                    filter {
                        eq("id", postId.toLong())
                    }
                }
            } catch (e: Exception) {
                Log.d("FeedRepository", "Couldn't Unlike Post: $e")
            }
        }

        override suspend fun addMeme(meme: MemeDTO): Int? {
            return try {
                val postId = database.from("feed_table").insert(meme) {
                    select(columns = Columns.list("id"))
                }.decodeSingle<Map<String, Int>>().values.first()
                Log.d("FeedRepository", "Added to Feed")
                postId
            } catch (e: Exception) {
                Log.d("FeedRepository", "Couldn't Add to Feed: $e")
                null
            }
        }


        override suspend fun getMemesByIds(idList: List<Int>): Flow<List<MemeDTO>> {


            Log.d("ProfileFlow", "getMemes called with idList of size $idList")
            val memeList = mutableListOf<MemeDTO>()
            var tempIndex = 0

            return flow<List<MemeDTO>> {

                while (tempIndex < idList.size){

                    for ( i in tempIndex..tempIndex + 10){

                        if (i<idList.size) {
                            val meme = getMemeById(idList[i])

                            memeList.add(meme)
                        }else{
                            break
                        }
                    }
                    emit(memeList)
                    Log.d("ProfileFlow", "Emitted list of ${memeList.size} memes: $memeList")
                    memeList.clear()
                    tempIndex = tempIndex + 11

                }

            }

            //Log.d("ProfileFunctions", "Meme of id $id fetched and converted to MemeObject: $meme")

        }


    }

