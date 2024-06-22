package eu.application.twotowers.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ExploreViewModelFactory (private val firebaseReteriverManager: FirebaseReteriverManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(ExploreViewModel ::class.java)){
            return ExploreViewModel(firebaseReteriverManager) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}