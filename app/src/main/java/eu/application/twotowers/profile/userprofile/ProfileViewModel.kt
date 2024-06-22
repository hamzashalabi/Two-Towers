package eu.application.twotowers.profile.userprofile

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel(private val userProfileRetriever: UserProfileRetriever) : ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    fun fetchUserProfile(callback : (User?) -> Unit) {
        userProfileRetriever.retrieveUserProfile { user->
            _userProfile.postValue(user)
            callback(user)
        }
    }

    fun nav(from : Activity, to : Class<out Activity>){
        val intent = Intent(from , to)
        from.startActivity(intent)
    }

}