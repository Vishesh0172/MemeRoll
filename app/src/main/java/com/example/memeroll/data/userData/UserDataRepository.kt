package com.example.memeroll.data.userData

import android.util.Log
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.model.UserDTO
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Inject


interface UserDataRepository{
    suspend fun createUser()
    suspend fun getUserById(id: String): UserDTO
    suspend fun getUserPosts(memeIds: List<Int>): List<MemeDTO>
    suspend fun addUserPost(userId: String, postId: Int)
}
class UserDataRepositoryImpl @Inject constructor(
    private val database: Postgrest,
    private val feedRepository: FeedDatabaseRepositoryImpl
) : UserDataRepository{
    override suspend fun createUser() {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(id: String): UserDTO {
        return try {
            database.from("users_table").select{
                filter {
                    eq("user_id", id)
                }
            }.decodeSingle<UserDTO>()
        }catch (e: Exception){
            Log.d("UserRepo", "Cant fetch User: $e")
            UserDTO()
        }
    }

    override suspend fun getUserPosts(memeIds: List<Int>): List<MemeDTO> {

        val memeList = mutableListOf<MemeDTO>()
        for (id in memeIds){
            val meme = feedRepository.getMemeById(id)
            memeList.add(meme)
        }
        return memeList
    }

    override suspend fun addUserPost(userId: String, postId: Int) {
        val memeIds = getUserById(userId).posts ?: emptyList<Int>()
        memeIds.toMutableList().add(postId)
        database.from("users_table").update(
            {
                set("user_posts", memeIds)
            }
        ){
            filter {
               eq("user_id", userId)
            }
        }
    }
}