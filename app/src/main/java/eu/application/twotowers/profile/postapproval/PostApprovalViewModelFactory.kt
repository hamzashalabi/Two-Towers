package eu.application.twotowers.profile.postapproval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PostApprovalViewModelFactory (private val firebasePostApproval: FirebasePostApproval): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(PostApprovalViewModel ::class.java)){
            return PostApprovalViewModel(firebasePostApproval) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}