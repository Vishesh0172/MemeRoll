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
import com.example.memeroll.data.userData.UserDataRepositoryImpl
import com.example.memeroll.model.UserDTO
import com.example.memeroll.work.MemeUploaderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val authRepositoryImpl: AuthRepositoryImpl,
    private val userRepository: UserDataRepositoryImpl
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

            user = UserDTO(
                userId = authRepositoryImpl.getCurrentUser()!!.id,
                userName = userRepository.getUserById(authRepositoryImpl.getCurrentUser()!!.id).userName,
            )

            workState.collect{ workInfoList ->
                for (workInfo in workInfoList){
                    transformUploadingList(workInfo)
                }

                workManager.pruneWork()

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
        }

    }



    private fun transformUploadingList(workInfo: WorkInfo){

        when(workInfo.state) {

            WorkInfo.State.SUCCEEDED -> {

                val uploadingMemeList = state.value.uploadingMemes.toMutableList()
                uploadingMemeList.remove(uploadingMemeList.find { it.workId == workInfo.id })
                Log.d("WorkState", "Collected Succeeded WorkInfo of $workInfo")
                _state.update { it.copy(uploadingMemeList) }
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
}

data class SharedState(
    val uploadingMemes: List<UploadingMeme> = emptyList(),
    val postUriString: String = "",
    val workIds: List<UUID> = emptyList()

)

data class UploadingMeme(
    val workId: UUID,
    val uri: Uri,
    var status: UploadingStatus
)

enum class UploadingStatus{
    SUCCESS, RUNNING, FAILURE
}