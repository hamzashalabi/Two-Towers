package eu.application.twotowers.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreatePostViewModelFactory (private val galleryHelper: GalleryHelper, private val databaseManager: DatabaseManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(CreatePostViewModel ::class.java)){
            return CreatePostViewModel(galleryHelper , databaseManager) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}