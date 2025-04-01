package com.example.memeroll.presentation.main.shared

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import androidx.work.hasKeyWithValueOfType
import androidx.work.workDataOf
import com.example.memeroll.authentication.AuthRepositoryImpl
import com.example.memeroll.data.FeedDatabaseRepositoryImpl
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.MemeDTO
import com.example.memeroll.model.UserDTO
import com.example.memeroll.work.MemeDeleteWorker
import com.example.memeroll.work.MemeUploaderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val authRepository: AuthRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl,
    private val feedRepository: FeedDatabaseRepositoryImpl,
) : ViewModel() {


    private val _state = MutableStateFlow(SharedState())
    val state = _state.asStateFlow()
    private lateinit var user: UserDTO
    private val workState = workManager.getWorkInfosLiveData(
        WorkQuery.fromIds(state.value.workIds)
    ).asFlow()

    init {

        workManager.pruneWork()

        viewModelScope.launch {

            // Flow to Collect WorkInfo for UploadingMemes
            launch {
                workState
                    .flowOn(Dispatchers.IO)
                    .collect { workInfoList ->
                    Log.d("WorkInfo Collected", workInfoList.toString())
                    for (workInfo in workInfoList) {
                        transformUploadingList(workInfo)
                    }
                    workManager.pruneWork()
                }
            }


            val userDeferred = async(Dispatchers.IO){
                authRepository.getCurrentUser()?.let {
                    userRepository.getUserById(it.id)
                }
            }
            val myUser = userDeferred.await()

            myUser?.let {

                // Updating state with user
                user = it
                val userPosts = user.posts ?: emptyList<Int>()
                val userLikedPosts = user.likedPosts ?: emptyList<Int>()
                val userName = it.userName
                _state.update { it.copy(numberOfPosts = userPosts.size, userName = userName) }
                Log.d("SharedViewModel", "${userLikedPosts.size} liked posts: $userLikedPosts")

                // Flow to Collect UserPosts only when user is not null
                launch(Dispatchers.IO){
                    feedRepository.getMemesByIds(userPosts)
                        .collect {
                            val userPostsList = state.value.userPosts.toMutableList()
                            userPostsList.addAll(it)
                            Log.d("UserPostFlow", "Collected ${it.size} Memes: $it")
                            _state.update { it.copy(userPosts = userPostsList) }
                        }
                }

                // Flow to Collect UserLikedPosts only when user is not null
                launch(Dispatchers.IO){
                    feedRepository.getMemesByIds(userLikedPosts)
                        .collect {
                            val likedPostsList = state.value.likedPosts.toMutableList()
                            likedPostsList.addAll(it)
                            Log.d("LikedPostsFlow", "Collected ${it.size} liked Memes: $it")
                            _state.update { it.copy(likedPosts = likedPostsList) }
                        }
                }

            }

        }


    }


    fun onSharedEvent(event: SharedEvent){

        when(event){

            SharedEvent.PostMeme -> {

                val workRequest = OneTimeWorkRequestBuilder<MemeUploaderWorker>()
                    .setInputData(
                        workDataOf(
                            "KEY_UID" to user.userId,
                            "KEY_URI" to state.value.postUriString,
                            "KEY_USERNAME" to user.userName
                        )
                    ).build()

                workManager.enqueue(workRequest)

                val workIds = state.value.workIds.toMutableList()
                workIds.add(workRequest.id)
                _state.update { it.copy(workIds = workIds, postUriString = "") }

                Log.d("SharedWork", "SharedViewModel: Posted to WorkManager with workIds ${state.value.workIds}")
            }


            is SharedEvent.CancelUpload -> {
                workManager.cancelWorkById(event.workId)
            }

            is SharedEvent.UpdateUri -> {
                _state.update { it.copy(postUriString = event.uri.toString()) }
            }

            is SharedEvent.DeleteMeme -> {

                if (state.value.selectedType == SelectedType.POSTS)
                    deletePostAndUpdateState(event.memeId, event.url)
                else
                    viewModelScope.launch {
                        removeFavoriteAndUpdateState(event.memeId)
                    }

            }

            is SharedEvent.ShowMeme -> {
                _state.update { it.copy(selectedMeme = event.meme) }
            }

            is SharedEvent.ChangeType -> {
                _state.update { it.copy(selectedType = event.type) }
            }

        }

    }



    private suspend fun transformUploadingList(workInfo: WorkInfo){

        when(workInfo.state) {

            WorkInfo.State.SUCCEEDED -> {

                val uploadingMemeList = state.value.uploadingMemes.toMutableList()
                val userPosts = state.value.userPosts.toMutableList()

                uploadingMemeList.removeIf{ it.workId == workInfo.id }
                val memeId = workInfo.outputData.getInt("POST_ID", 0)
                feedRepository.getMemeById(memeId)?.let {
                    userPosts.add(it)
                }

                Log.d("WorkState", "Collected Succeeded WorkInfo of $workInfo")
                _state.update { it.copy(uploadingMemes = uploadingMemeList, userPosts = userPosts) }
            }

            WorkInfo.State.RUNNING -> {

                if (workInfo.progress.hasKeyWithValueOfType<String>("URI")) {
                    val uriString = workInfo.progress.getString("URI") ?: ""
                    val uri = Uri.parse(uriString)
                    val uploadingMemeList = state.value.uploadingMemes.toMutableList()
                    uploadingMemeList.add(
                        UploadingMeme(
                            workId = workInfo.id,
                            uri = uri,
                            status = UploadingStatus.RUNNING
                        )
                    )
                    Log.d("WorkState", "Collected Running WorkInfo of $workInfo")
                    _state.update { it.copy(uploadingMemeList) }
                }
            }

            else -> {}
        }

    }

    private fun getPathFromUrl(url: String): String{
        val urlPrefix = "https://bgcrldfhsleabdynwmgg.supabase.co/storage/v1/object/"
        return url.removePrefix(urlPrefix)
    }

    private fun deletePostAndUpdateState(memeId: Int?, url: String){

        val posts = state.value.userPosts.toMutableList()

        val workRequest = OneTimeWorkRequestBuilder<MemeDeleteWorker>()
            .setInputData(
                workDataOf(
                    MemeDeleteWorker.MEME_ID to memeId,
                    MemeDeleteWorker.USER_ID to user.userId,
                    MemeDeleteWorker.PATH to getPathFromUrl(url)
                )
            ).build()

        workManager.enqueue(workRequest)

        posts.removeIf{it.id == memeId}
        _state.update { it.copy(userPosts = posts) }
    }

    private suspend fun removeFavoriteAndUpdateState(memeId: Int){

        val likedPosts = state.value.likedPosts.toMutableList()

        withContext(Dispatchers.IO){ userRepository.removeFromLikedPosts(memeId, user.userId) }

        likedPosts.removeIf{it.id == memeId}
        _state.update { it.copy(likedPosts = likedPosts) }
    }
}

data class SharedState(

    val uploadingMemes: List<UploadingMeme> = emptyList(),
    val userPosts: List<MemeDTO> = emptyList(),
    val likedPosts: List<MemeDTO> = emptyList(),
    val postUriString: String = "",
    val workIds: List<UUID> = emptyList(),
    val userName: String = "",
    val numberOfPosts: Int = 0,
    val selectedMeme: MemeDTO? = null,
    val selectedType: SelectedType = SelectedType.POSTS

)

data class UploadingMeme(
    val memeId: Int? = null,
    val workId: UUID,
    val uri: Uri,
    var status: UploadingStatus
)

enum class UploadingStatus{
    SUCCESS, RUNNING, FAILURE
}

enum class SelectedType(val typeName: String){
    POSTS("Posts"), FAVORITES("Favorites")
}