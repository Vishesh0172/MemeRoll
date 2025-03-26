package com.example.memeroll.data.userData

import android.util.Log
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.model.UserDTO
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject


interface UserDataRepository{
    suspend fun createUser(name: String, userId: String)
    suspend fun getUserById(id: String): UserDTO
    suspend fun getUserPosts(memeIds: List<Int>): List<MemeDTO>
    suspend fun addUserPost(userId: String, postId: Int)
    suspend fun addToLikedPosts(postId: Int, userId: String)
    suspend fun removeFromLikedPosts(postId: Int, userId: String)
    suspend fun checkIfLiked(postId: Int, userId: String): Boolean
}


class UserDataRepositoryImpl @Inject constructor(
    private val database: Postgrest
) : UserDataRepository{
    override suspend fun createUser(name: String, userId: String) {
        database.from("users_table").insert(
            UserDTO(
                userId = userId,
                userName = name,
                posts = emptyList<Int>()
            ),
            {
                defaultToNull = false
            }
        )
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
//        for (id in memeIds){
//            val meme = feedRepository.getMemeById(id)
//            memeList.add(meme)
//        }

        return memeList
    }

    override suspend fun addUserPost(userId: String, postId: Int) {
        val memeIds = (getUserById(userId).posts ?: emptyList<Int>()).toMutableList()
        memeIds.add(postId)
        try {
            database.from("users_table").update(
                {
                    set("user_posts", memeIds)
                }
            ){
                filter {
                    eq("user_id", userId)
                }
            }
            Log.d("UserRepository", "Post Added to User")
        }catch (e: Exception){
            Log.d("UserRepository", "Cant add post to User: $e")
        }

    }

    override suspend fun addToLikedPosts(postId: Int, userId: String) {
        val likedMemeIds = (getUserById(userId).likedPosts ?: emptyList<Int>()).toMutableList()
        likedMemeIds.add(postId)
        try {
            database.from("users_table").update(
                {
                    set("liked_memes", likedMemeIds)
                }
            ){
                filter {
                    eq("user_id", userId)
                }
            }
            Log.d("UserRepository", "Post Added to LikedPosts")
        }catch (e: Exception){
            Log.d("UserRepository", "Cant add likedPost to User: $e")
        }
    }

    override suspend fun removeFromLikedPosts(postId: Int, userId: String) {
        val likedMemeIds = (getUserById(userId).likedPosts ?: emptyList<Int>()).toMutableList()
        likedMemeIds.remove(postId)
        try {
            database.from("users_table").update(
                {
                    set("liked_memes", likedMemeIds)
                }
            ){
                filter {
                    eq("user_id", userId)
                }
            }
            Log.d("UserRepository", "Post Removed from likedPosts")
        }catch (e: Exception){
            Log.d("UserRepository", "Cant remove from likePosts: $e")
        }
    }

    override suspend fun checkIfLiked(postId: Int, userId: String): Boolean {

        val likedMemeIds = (getUserById(userId).likedPosts ?: emptyList<Int>())
//        val likeList = database.from("users_table").select(Columns.list("liked_memes")){
//            filter {
//                eq("user_id", userId)
//            }
//        }.decodeList<Int>()

        return postId in likedMemeIds
    }
}