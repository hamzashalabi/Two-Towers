package eu.application.twotowers.profile.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PostViewModelFactory (private val firebasePostRetriever: FirebasePostRetriever): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(PostViewModel ::class.java)){
            return PostViewModel(firebasePostRetriever) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}