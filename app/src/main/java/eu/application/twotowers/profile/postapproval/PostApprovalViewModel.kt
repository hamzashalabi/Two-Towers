package eu.application.twotowers.profile.postapproval

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostApprovalViewModel(private val firebasePostApproval: FirebasePostApproval) : ViewModel() {


    private val _postList = MutableLiveData<List<PostInfo?>>()
    val postList: LiveData<List<PostInfo?>> = _postList

    fun fetchAllPosts(){
        firebasePostApproval.retrieveAllPosts { postList->
            _postList.postValue(postList)
        }
    }

    fun approvedPost(approvalResult: ApprovalResult){
        firebasePostApproval.postApproval(approvalResult)
    }

    fun updateWarning(approvalResult: ApprovalResult){
        firebasePostApproval.updateWarning(approvalResult)
    }
}