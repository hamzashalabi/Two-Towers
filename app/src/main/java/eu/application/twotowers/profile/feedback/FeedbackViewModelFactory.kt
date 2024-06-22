package eu.application.twotowers.profile.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FeedbackViewModelFactory (private val firebaseFeedback: FirebaseFeedback): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(FeedbackViewModel ::class.java)){
            return FeedbackViewModel(firebaseFeedback) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}