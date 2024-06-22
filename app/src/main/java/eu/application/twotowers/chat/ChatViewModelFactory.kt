package eu.application.twotowers.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory (private val firebaseReteriver: FirebaseReteriver): ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(ChatViewModel ::class.java)){
            return ChatViewModel(firebaseReteriver) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}
