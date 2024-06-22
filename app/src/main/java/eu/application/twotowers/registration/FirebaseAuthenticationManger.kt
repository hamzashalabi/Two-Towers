package eu.application.twotowers.registration


import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage

class FirebaseAuthenticationManger(private val auth : FirebaseAuth) : AuthenticationManager{

    private val failure : Boolean =false
    override fun login(email: String, password: String, callback: (success: Boolean) -> Unit) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            callback(failure)
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }
        }
    }

    override fun registration(userName: String, email: String, password: String, callback: (success: Boolean) -> Unit) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty() || userName.isNullOrEmpty()) {
            callback(failure)

        } else {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                callback(task.isSuccessful)

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let { firebaseUser ->
                        saveUserData(firebaseUser.uid, userName, email)
                    }
                }
            }

        }
    }

    private fun saveUserData (userID:String, userName: String, email: String){

        val database= Firebase.database
        val role = "user"
        val warning = 0
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        val userRef = usersRef.child(userID)
        val userData = mapOf(
            "name" to userName,
            "email" to email,
            "role" to role,
            "warning" to warning
        )

        userRef.setValue(userData)

    }

    override fun userImage(userImage: String) {
        val fileName = "userImage.jpg"
        val reference = FirebaseStorage.getInstance().getReference("${auth.currentUser?.uid}").child(fileName)
        val uploadTask = reference.putFile(userImage.toUri())

        uploadTask.addOnSuccessListener {
            Log.e("Tag","Success")
        }.addOnFailureListener{
            Log.e("Tag","Failure")
        }

    }

}
