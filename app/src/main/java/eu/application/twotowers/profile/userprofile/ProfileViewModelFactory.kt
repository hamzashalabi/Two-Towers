package eu.application.twotowers.profile.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ProfileViewModelFactory (private val userProfileRetriever: UserProfileRetriever): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(ProfileViewModel ::class.java)){
            return ProfileViewModel(userProfileRetriever) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}