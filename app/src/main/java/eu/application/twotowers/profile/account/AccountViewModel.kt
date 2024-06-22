package eu.application.twotowers.profile.account

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel

class AccountViewModel(private val firebaseAccountEditor: FirebaseAccountEditor) : ViewModel() {

    fun changePassword(oldPassword: String , email : String , newPassword: String) {
        firebaseAccountEditor.changePassword(oldPassword , email , newPassword)
    }

    fun changeUserName(newName: String) {
        firebaseAccountEditor.changeName(newName)
    }

    fun deleteAccount(email: String , password : String) {
        firebaseAccountEditor.deleteAccount(email , password)
    }

    fun signOut() {
        firebaseAccountEditor.signOut()
    }

    fun nav(from : Activity, to : Class<out Activity>){
        val intent = Intent(from , to)
        from.startActivity(intent)
    }



}