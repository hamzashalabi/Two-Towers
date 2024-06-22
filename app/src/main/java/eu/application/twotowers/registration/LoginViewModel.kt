package eu.application.twotowers.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel(private val authManager: AuthenticationManager): ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _loginStatus = MutableLiveData<LoginStatus>()
    val loginStatus : LiveData<LoginStatus> = _loginStatus

    fun onLoginClicked () {
        val email = email.value ?: return
        val password = password.value ?: return
        authManager.login(email,password){ success ->
            _loginStatus.value = if(success) LoginStatus.SUCCESS else LoginStatus.FAILURE
        }
    }
}

