package eu.application.twotowers.profile.feedback

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.concurrent.CountDownLatch

class FirebaseFeedbackManager : FirebaseFeedback {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    override fun sendFeedback(feedback: Feedback , callback: (success : Boolean) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("Feedback/$uid").push()

        val success = true
        val failure = false

        if (feedback.feedback.isNullOrEmpty() || feedback.userName.isNullOrEmpty()){
            callback(failure)
        }else{
            callback(success)
        val feedbackData = mapOf(
            "feedback" to feedback.feedback,
            "User Name" to feedback.userName
        )
        reference.setValue(feedbackData)
        }
    }

    override fun retrieveFeedbackAdmin(callback: (List<Feedback>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("Feedback")
        val feedbackList = mutableListOf<Feedback>()

        reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val countDownLatch = CountDownLatch(snapshot.childrenCount.toInt())
                val uid = snapshot.key
                for(feedbackSnapshot in snapshot.children) {
                    val feedbackMessage = feedbackSnapshot.child("feedback").getValue(String::class.java)
                    val userName = feedbackSnapshot.child("User Name").getValue(String::class.java)
                    Log.e("admin feedback","$feedbackMessage / $userName")
                    getUserImage(uid.toString()){userImage->
                        val feedback = Feedback(feedbackMessage , userName , userImage)
                        feedbackList.add(feedback)
                        countDownLatch.countDown()
                        if(countDownLatch.count == 0L){
                            callback(feedbackList)
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }


        } )
    }

    override fun retrieveFeedbackUser(callback: (List<Feedback>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("Feedback/$uid")
        val feedbackList = mutableListOf<Feedback>()
        Log.e("Feedback" , "uid : $uid")
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val feedbackMessage = snapshot.child("feedback").getValue(String::class.java)
                    val feedback = Feedback(feedbackMessage)
                    feedbackList.add(feedback)
                        callback(feedbackList)
                        Log.e("Feedback" , "uid : $feedbackList")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
                Log.e("Feedback" , "Error : $error")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
        })

    }

    private fun getUserImage(uid : String , callback: (Uri?) -> Unit){
        val reference = FirebaseStorage.getInstance().reference

        // TODO("fix image path")
        val path = "$uid/userImage.jpg.webp"
        var imageUri : Uri?
        val imageRef = reference.child(path)
        val localFile = File.createTempFile("images" , "jpg")

        imageRef.getFile(localFile).addOnSuccessListener {
            Log.e("image Firebase" , "Success ")
            imageUri = Uri.fromFile(localFile)
            callback(imageUri)
        }.addOnFailureListener {
            Log.e("image Firebase" , "Failure : $it")
            callback(null)
        }

    }

}