package com.example.memeroll.data

import android.util.Log
import com.example.memeroll.model.MemeDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface FeedDatabaseRepository {

    suspend fun getFeedMeme(from: Long): List<MemeDTO>?
    suspend fun getMemeById(id: Int): MemeDTO?
    suspend fun likeMeme(postId: Int)
    suspend fun unlikeMeme(postId: Int)
    suspend fun addMeme(meme: MemeDTO): Int?
    suspend fun deleteMeme(memeId: Int): Boolean
    suspend fun getMemesByIds(idList: List<Int>): Flow<List<MemeDTO>>
}

    class FeedDatabaseRepositoryImpl @Inject constructor(
        private val database: Postgrest
    ) : FeedDatabaseRepository {



        override suspend fun getFeedMeme(from: Long): List<MemeDTO>? {
            return try {
                val list = database.from("feed_table").select {
                    range(from = from, to = from)

                }.decodeList<MemeDTO>()
                Log.d("MemeList", list.toString())
                list
            } catch (e: Exception) {
                Log.d("FeedRepository", "Exception: $e")
                null
            }
        }

        override suspend fun getMemeById(id: Int): MemeDTO? {
            return try {
                database.from("feed_table").select {
                    filter {
                        eq("id", id)
                    }
                }.decodeSingle<MemeDTO>()
            }catch (e: Exception){
                Log.d("FeedRepository","Couldn't fetch meme by Id: $e")
                null
            }
        }

        override suspend fun likeMeme(postId: Int) {

            Log.d("FeedRepository", "$postId")
            try {
                val meme = getMemeById(postId) ?: throw NullPointerException()
                database.from("feed_table").update(
                    {
                        set("like_count", meme.likeCount.toLong().plus(1L))
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
                val meme = getMemeById(postId) ?: throw NullPointerException()
                database.from("feed_table").update(
                    {
                        set("like_count", meme.likeCount.toLong().minus(1L))
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

        override suspend fun deleteMeme(memeId: Int): Boolean {
           return try {
               database.from("feed_table").delete{
                   filter { eq("id", memeId) }
               }
               Log.d("FeedRepository", "Removed meme $memeId from Feed")
               true
           }catch (e: Exception){
               Log.d("FeedRepository", "Couldn't remove meme of Id $memeId from Feed: $e")
               false
           }
        }


        override suspend fun getMemesByIds(idList: List<Int>): Flow<List<MemeDTO>> {


            Log.d("ProfileFlow", "getMemes called with idList of size $idList")
            val memeList = mutableListOf<MemeDTO>()
            var tempIndex = 0

            return flow<List<MemeDTO>> {

                while (tempIndex < idList.size) {

                    for (i in tempIndex..tempIndex + 10) {

                        if (i < idList.size) {
                            val meme = getMemeById(idList[i])
                            meme?.let { memeList.add(it) }
                        } else {
                            break
                        }
                    }
                    emit(memeList)
                    Log.d("ProfileFlow", "Emitted list of ${memeList.size} memes: $memeList")
                    memeList.clear()
                    tempIndex = tempIndex + 11

                }

            }

        }

    }