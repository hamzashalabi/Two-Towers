package eu.application.twotowers.registration


import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.application.twotowers.create.GalleryHelper


class RegistrationViewModel (private val authManager : AuthenticationManager ,
                             private val registerGalleryHelper: RegisterGalleryHelper) : ViewModel() {
    val email = MutableLiveData<String>()

    val password = MutableLiveData<String>()

    val userName = MutableLiveData<String>()

    private val _registrationStatus = MutableLiveData<RegistrationStatus>()
    val registrationStatus: LiveData<RegistrationStatus> = _registrationStatus

    fun onSignedUpClicked() {
        val email = email.value ?: return
        val password = password.value ?: return
        val userName = userName.value ?: return
        authManager.registration(userName, email, password) { success ->
                _registrationStatus.value=if (success) RegistrationStatus.SUCCESS else RegistrationStatus.FAILURE
        }
    }

    fun saveUserImage(userImage : String){
        authManager.userImage(userImage)
    }

    fun openGallery(){
        registerGalleryHelper.openGallery()
    }

    fun handleGalleryResult(requestCode : Int, resultCode : Int, data : Intent?, callback: RegisterGalleryHelper.GalleryCallbackManger){
        registerGalleryHelper.handleActivityResult(requestCode ,resultCode , data , callback)
    }

}
