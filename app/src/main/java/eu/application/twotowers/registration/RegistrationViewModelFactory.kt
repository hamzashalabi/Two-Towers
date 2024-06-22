package eu.application.twotowers.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegistrationViewModelFactory (private val authManager: AuthenticationManager ,
                                    private val registerGalleryHelper: RegisterGalleryHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(authManager , registerGalleryHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}