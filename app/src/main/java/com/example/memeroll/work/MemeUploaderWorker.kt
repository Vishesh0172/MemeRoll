package com.example.memeroll.work

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.StorageRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.MemeDTO
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MemeUploaderWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val storageRepository: StorageRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl,
    private val feedRepository: FeedDatabaseRepositoryImpl
): CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {


        val stringUri = workerParams.inputData.getString("KEY_URI")
        val userId = workerParams.inputData.getString("KEY_UID")!!
        val userName = workerParams.inputData.getString("KEY_USERNAME")!!
        setProgress(workDataOf("PROGRESS" to true, "URI" to stringUri))

        val uri = Uri.parse(stringUri)

        val imgUrl = storageRepository.addImage(uri = uri, userId = userId) ?: return Result.failure(
            workDataOf("RESULT_STATUS" to "FAILURE", "URI" to stringUri)
        )




        val meme = MemeDTO(imgUrl = imgUrl, userName = userName)
        val postId = feedRepository.addMeme(meme) ?: return Result.failure(
            workDataOf("RESULT_STATUS" to "FAILURE", "URI" to stringUri)
        )


        userRepository.addUserPost(userId = userId, postId = postId)


        return Result.success(workDataOf(
            "RESULT_STATUS" to "SUCCESS",
            "URI" to stringUri,
            "POST_ID" to postId
        ))

    }


}

