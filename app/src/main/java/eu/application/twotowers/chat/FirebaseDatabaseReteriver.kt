package eu.application.twotowers.chat

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

class FirebaseDatabaseReteriver : FirebaseReteriver {
    private val failure : Boolean = false
    private val success : Boolean = true
    override fun retrieveUserData(callback: (List<User?>) -> Unit) {
        val users = mutableListOf<User>()
        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val countDownLatch = CountDownLatch(snapshot.childrenCount.toInt())
                for(userSnapshot in snapshot.children ){
                val uid = userSnapshot.key
                val userName = userSnapshot.child("name").getValue(String::class.java)

                getUserImage(uid.toString()){image ->
                    val user = User(userName ,image , uid)
                    users.add(user)
                    countDownLatch.countDown()
                    if(countDownLatch.count == 0L)
                        callback(users)
                }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase Class","database error : $error")
                callback(emptyList())
            }

        })
    }

    override fun sendConversation(message: Message ,callBack : (success :Boolean)->Unit) {
        val reference = FirebaseDatabase.getInstance().reference
        if(message.message.isNullOrEmpty()||message.fromId.isNullOrEmpty()||message.toId.isNullOrEmpty())
            callBack(failure)
        else {
            callBack(success)
            val fromReference = reference.child("Messages/${message.fromId}/${message.toId}").push()
            val toReference = reference.child("Messages/${message.toId}/${message.fromId}").push()
            val fromLatestMessage = reference.child("Latest Message/${message.fromId}/${message.toId}")
            val toLatestMessage = reference.child("Latest Message/${message.toId}/${message.fromId}")


            val messageData = mapOf(
                "Message" to message.message,
                "From" to message.fromId,
                "To" to message.toId
            )

            fromReference.setValue(messageData).addOnSuccessListener {
                Log.e("send message","success")
            }.addOnFailureListener{
                Log.e("send message","failure")
            }

            toReference.setValue(messageData).addOnSuccessListener {
                Log.e("reverse send message","success")
            }.addOnFailureListener{
                Log.e("reverse send message","failure")
            }

            fromLatestMessage.setValue(messageData).addOnSuccessListener {
                Log.e("latest send message","success")
            }.addOnFailureListener{
                Log.e("latest send message","failure")
            }

            toLatestMessage.setValue(messageData).addOnSuccessListener {
                Log.e("reverse latest send message","success")
            }.addOnFailureListener{
                Log.e("reverse latest send message","failure")
            }
        }

    }

    override fun retrieveConversation(callback: (MutableList<Message?>) -> Unit) {
        val fromId = ChatActivity.ToIdHolder.fromId
        val toId = ChatActivity.ToIdHolder.toId
        val reference = FirebaseDatabase.getInstance().getReference("Messages/${fromId}/${toId}")
        val messageList = mutableListOf<Message?>()

        reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.child("Message").getValue(String::class.java)
                val from = snapshot.child("From").getValue(String::class.java)
                val to = snapshot.child("To").getValue(String::class.java)
                val mMessage = Message(message , from , to)
                messageList.add(mMessage)
                callback(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("error in message ", error.message)
                callback(emptyList<Message>().toMutableList())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
        })
    }

    override fun retrieveLatestMessage(callback: (MutableList<Message?>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("Latest Message/${FirebaseAuth.getInstance().currentUser?.uid}")
        val messageList = mutableListOf<Message?>()

        reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("latest message inside on child added","called again")
                val message = snapshot.child("Message").getValue(String::class.java)
                val from = snapshot.child("From").getValue(String::class.java)
                val to = snapshot.child("To").getValue(String::class.java)
                val mMessage = Message(message , from , to)
                messageList.add(mMessage)
                callback(messageList)
                Log.e("FirebaseDatabaseReteriver", "onChildAdded callback invoked")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                messageList.clear()
                messageList.add(message)
                callback(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList<Message?>().toMutableList())
                Log.e("error in latest message",error.message)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

        })
    }

    override fun retrieveCurrentUserImage(callback: (Uri?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
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

    private fun getUserImage(userId : String , callback: (Uri?) -> Unit){
        val reference = FirebaseStorage.getInstance().reference

        val path = "$userId/userImage.jpg"
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