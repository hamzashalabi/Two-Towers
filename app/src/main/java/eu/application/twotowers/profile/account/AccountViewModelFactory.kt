package eu.application.twotowers.profile.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AccountViewModelFactory (private val firebaseAccountEditor: FirebaseAccountEditor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>):T{
        if(modelClass.isAssignableFrom(AccountViewModel::class.java)){
            return AccountViewModel(firebaseAccountEditor) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}