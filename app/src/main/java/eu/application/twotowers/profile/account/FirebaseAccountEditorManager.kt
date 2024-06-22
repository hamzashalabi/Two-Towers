package eu.application.twotowers.profile.account

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class FirebaseAccountEditorManager : FirebaseAccountEditor {
    val user = FirebaseAuth.getInstance().currentUser


    override fun changePassword(oldPassword: String, email: String, newPassword: String) {
        val credential= EmailAuthProvider.getCredential(email , oldPassword)

        user?.reauthenticate(credential)?.addOnSuccessListener {
            user.updatePassword(newPassword)
            Log.e("FirebaseAccountEditorManager", "change Password: success ")
        }?.addOnFailureListener {
            Log.e("FirebaseAccountEditorManager", "change Password: ", it)
        }
    }
    override fun changeName(newName: String) {
        val reference = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/name")

        reference.setValue(newName).addOnSuccessListener {
            Log.e("FirebaseAccountEditorManager", "change Name: success ")
        }.addOnFailureListener{
            Log.e("FirebaseAccountEditorManager", "change Name: ", it)
        }
    }


    override fun deleteAccount(email: String , password : String) {
        val reference = FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
        val credential= EmailAuthProvider.getCredential(email , password)
        user?.reauthenticate(credential)?.addOnSuccessListener {
            reference.removeValue().addOnSuccessListener {
                Log.e("FirebaseAccountEditorManager", "delete Account: success ")
            }.addOnFailureListener{
                Log.e("FirebaseAccountEditorManager", "delete Account: ", it)
            }
            user.delete().addOnSuccessListener {
                Log.e("FirebaseAccountEditorManager", "delete Account: success ")
                FirebaseAuth.getInstance().signOut()
            }
        }?.addOnFailureListener{
            Log.e("FirebaseAccountEditorManager", "delete Account: ", it)
        }
    }

    override fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}