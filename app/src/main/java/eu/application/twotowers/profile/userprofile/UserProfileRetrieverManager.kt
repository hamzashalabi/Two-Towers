package eu.application.twotowers.profile.userprofile

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UserProfileRetrieverManager : UserProfileRetriever {
    val uid = FirebaseAuth.getInstance().uid
    override fun retrieveUserProfile(callback: (User?) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("users/$uid")

        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java)
                val userRole = snapshot.child("role").getValue(String::class.java)
                val warningCount = snapshot.child("warning").getValue(Int::class.java)
                userImage(uid.toString()){userImage->
                    val user = User(userName, userImage , userRole , warningCount)
                    callback(user)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
                Log.e("user profile database ","$error")
            }

        })
    }

    fun userImage(uid: String , callback: (Uri?) -> Unit){
        val reference = FirebaseStorage.getInstance().reference

        val path = "$uid/userImage.jpg"
        var imageUri : Uri?
        val imageRef = reference.child(path)
        val localFile = File.createTempFile("images" , "jpg")

        imageRef.getFile(localFile).addOnSuccessListener{
            Log.e("image Firebase" , "Success ")
            imageUri = Uri.fromFile(localFile)
            callback(imageUri)
        }.addOnFailureListener{
            Log.e("image Firebase" , "Failure : $it")
            callback(null)
        }
    }
}