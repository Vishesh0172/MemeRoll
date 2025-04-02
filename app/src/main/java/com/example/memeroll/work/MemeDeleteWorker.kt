package com.example.memeroll.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.StorageRepositoryImpl
import com.example.memeroll.data.UserDataRepositoryImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MemeDeleteWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val storageRepository: StorageRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl,
    private val feedRepository: FeedDatabaseRepositoryImpl
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {

        val userId = workerParams.inputData.getString(USER_ID) ?: ""
        val postId = workerParams.inputData.getInt(MEME_ID, 0)
        val path = workerParams.inputData.getString(PATH) ?: ""

        if (!userRepository.deleteUserPost(userId = userId, postId = postId))
            return Result.failure()
        else {
            feedRepository.deleteMeme(memeId = postId)
            storageRepository.deleteImage(
                userId = userId,
                path = path
            )
        }

        return Result.success()
    }

    companion object{
        const val USER_ID = "USER_ID"
        const val MEME_ID = "MEME_ID"
        const val PATH = "PATH"
    }


}
