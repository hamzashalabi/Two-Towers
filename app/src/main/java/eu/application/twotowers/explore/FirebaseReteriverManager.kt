package eu.application.twotowers.explore

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch

@RequiresApi(Build.VERSION_CODES.O)
class FirebaseReteriverManager: FirebaseReteriver {
    private val reference = FirebaseDatabase.getInstance().reference
    private var uidOrder : String? = null
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun retrievePostInfo(callback : (MutableList<PostInfo?>)->Unit){
        val allPosts : MutableList<PostInfo?> = mutableListOf()
        val userReference = reference.child("users")

        val query = if(uidOrder == null){
            userReference.orderByKey()
        }else {
            userReference.startAfter(uidOrder)
        }

        query.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                uidOrder = snapshot.key
                val uid = snapshot.key
                val userName = snapshot.child("name").getValue(String::class.java)
                val postReference = snapshot.child("posts")

                for(postSnapshot in postReference.children){
                    val postStatus = postSnapshot.child("Post Status").getValue(String::class.java)
                    if(postStatus == "pending"){
                        Log.e("post","pending")
                        continue
                    }

                    val postId = postSnapshot.key
                    val description = postSnapshot.child("Description").getValue(String::class.java)
                    val tempTimeStamp = postSnapshot.child("Time Stamp").getValue(Long::class.java)

                    val dateTime = LocalDateTime.ofInstant(tempTimeStamp?.let {
                        Instant.ofEpochMilli(
                            it
                        )
                    }, ZoneId.of("Asia/Amman"))
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val timestamp = formatter.format(dateTime)
                    val city = postSnapshot.child("City").getValue(String::class.java)
                    val area = postSnapshot.child("Area").getValue(String::class.java)
                    val numRooms = postSnapshot.child("Number Of Rooms").getValue(String::class.java)
                    val numBathrooms = postSnapshot.child("Number Of Bathrooms").getValue(String::class.java)
                    val price = postSnapshot.child("Price").getValue(String::class.java)
                    val propertyOptions = postSnapshot.child("Property Options").getValue(String::class.java)

                    retrievePostImages(uid.toString() , postId.toString()){images->
                        retrieveCurrentUsersImages(uid.toString()){userImage->
                            val post = PostInfo(description , timestamp , city , area , numRooms ,
                                numBathrooms , price , propertyOptions , userName , images , uid , postId.toString() , userImage)
                            allPosts.add(post)
                                callback(allPosts)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList<PostInfo?>().toMutableList())
                Log.e("post retrieval error ","ERROR : $error")
            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
        })
    }


    override fun likedPost(reference: LikeReference, callback: (success: Boolean) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid").child("Likes Reference").push()
        val failure = false
        val success = true
        val key = userRef.key

        if(reference.pid.isNullOrEmpty() || reference.uid.isNullOrEmpty()){
            callback(failure)
        }else{
            callback(success)
            val referenceData = mapOf(
                "User Id" to reference.uid,
                "Post Id" to reference.pid,
                "Key" to key
            )

            userRef.setValue(referenceData)
        }
    }


    override fun retrieveCurrentUserImage(callback: (Uri?) -> Unit) {
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


    fun retrievePostImages(uId : String? , pId :String? , callback: (List<Uri?>?) -> Unit) {
        val storage = FirebaseStorage.getInstance().reference


        val imagePaths = listOf(
            "${uId}/${pId}_images_0.jpg",
            "${uId}/${pId}_images_1.jpg",
            "${uId}/${pId}_images_2.jpg")


        val imagesFiles = mutableListOf<File>()
        val imagesUri = mutableListOf<Uri?>()
        val countDownLatch = CountDownLatch(imagePaths.size)

        for (imagePath in imagePaths) {
            val imagesRef = storage.child(imagePath)
            val localFile = File.createTempFile("images", "jpg")

            imagesRef.getFile(localFile).addOnSuccessListener {
                imagesFiles.add(localFile)
                imagesUri.add(Uri.fromFile(localFile))
                countDownLatch.countDown()
                if(countDownLatch.count == 0L){
                callback(imagesUri)
                }

            }.addOnFailureListener {
                Log.e("Error", "Database Error image error")
                countDownLatch.countDown()
                if(countDownLatch.count == 0L){
                    callback(emptyList())
                }

            }
        }
    }

    private fun retrieveCurrentUsersImages(uid : String , callback: (Uri?) -> Unit) {
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