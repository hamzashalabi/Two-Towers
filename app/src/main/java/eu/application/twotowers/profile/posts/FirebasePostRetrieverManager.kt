package eu.application.twotowers.profile.posts

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import eu.application.twotowers.explore.LikeReference
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch

@RequiresApi(Build.VERSION_CODES.O)
class FirebasePostRetrieverManager : FirebasePostRetriever {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun retrieveMyPosts(callback: (List<PostInfo?>) -> Unit) {
       val reference = FirebaseDatabase.getInstance().getReference("users/$uid/posts")
       val postList = mutableListOf<PostInfo?>()

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val countDownLatch = CountDownLatch(snapshot.childrenCount.toInt())
                for (snapshot in snapshot.children) {
                    val pid = snapshot.key
                    val description = snapshot.child("Description").getValue(String::class.java)
                    val city = snapshot.child("City").getValue(String::class.java)
                    val area = snapshot.child("Area").getValue(String::class.java)
                    val numRooms = snapshot.child("Number Of Rooms").getValue(String::class.java)
                    val numBathrooms =
                        snapshot.child("Number Of Bathrooms").getValue(String::class.java)
                    val price = snapshot.child("Price").getValue(String::class.java)
                    val propertyOptions =
                        snapshot.child("Property Options").getValue(String::class.java)
                    val tempTimeStamp = snapshot.child("Time Stamp").getValue(Long::class.java)
                    val dateTime = LocalDateTime.ofInstant(tempTimeStamp?.let {
                        Instant.ofEpochMilli(
                            it
                        )
                    }, ZoneId.of("Asia/Amman"))
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val timestamp = formatter.format(dateTime)

                    retrieveHouseImages(uid.toString(), pid.toString()) { houseImages ->
                        val post = PostInfo(
                            description,
                            timestamp,
                            city,
                            area,
                            numRooms,
                            numBathrooms,
                            price,
                            propertyOptions,
                            houseImages,
                            null,
                            null,
                            pid
                        )
                        Log.d("FirebasePostRetrieverManager", "postList: $post")
                        postList.add(post)
                        countDownLatch.countDown()
                        if (countDownLatch.count == 0L) {
                            callback(postList)
                            Log.d("FirebasePostRetrieverManager", "postList: $postList")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
                Log.e("FirebasePostRetrieverManager", "Failed to retrieve posts", error.toException())
            }

        })
    }

    override fun retrieveSavedPosts(callback: (List<LikedPostInfo?>) -> Unit) {
        val postList = mutableListOf<LikedPostInfo?>()
        retrieveSavedPostsReference {referenceList->
            referenceList.forEach { reference->
                Log.e("reference","$reference")
                val postReference = FirebaseDatabase.getInstance().getReference("users/${reference.uid}/posts/${reference.pid}")

                postReference.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                                val description = snapshot.child("Description").getValue(String::class.java)
                                val city = snapshot.child("City").getValue(String::class.java)
                                val area = snapshot.child("Area").getValue(String::class.java)
                                val numRooms = snapshot.child("Number Of Rooms").getValue(String::class.java)
                                val numBathrooms = snapshot.child("Number Of Bathrooms").getValue(String::class.java)
                                val price = snapshot.child("Price").getValue(String::class.java)
                                val propertyOptions = snapshot.child("Property Options").getValue(String::class.java)
                                val tempTimeStamp = snapshot.child("Time Stamp").getValue(Long::class.java)

                                val timestamp = tempTimeStamp?.let {
                                        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("Asia/Amman"))
                                        dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                }
                                retrieveHouseImages(reference.uid.toString(), reference.pid.toString()) { houseImages ->
                                    userImage(reference.uid.toString()){userImage->
                                        userName(reference.uid.toString()){userName ->
                                        val post = LikedPostInfo(
                                            description,
                                            timestamp,
                                            city,
                                            area,
                                            numRooms,
                                            numBathrooms,
                                            price,
                                            propertyOptions,
                                            houseImages,
                                            userName,
                                            userImage,
                                            reference.key

                                        )
                                        Log.e("post","$post")
                                        postList.add(post)
                                            callback(postList)
                                    }
                                }
                            }
                    }


                    override fun onCancelled(error: DatabaseError) {
                       callback(emptyList())
                        Log.e("callback","failure : $error")
                    }

                })
            }
        }
    }

    override fun deletePost(pId :String){
        val reference = FirebaseDatabase.getInstance().getReference("users/$uid/posts/$pId")
        reference.removeValue().addOnSuccessListener {
            Log.d("FirebasePostRetrieverManager", "Post deleted successfully")
        }.addOnFailureListener {
            Log.e("FirebasePostRetrieverManager", "Failed to delete post", it)
        }
    }

    override fun unlikePost(key : String) {
        val reference = FirebaseDatabase.getInstance().getReference("users/$uid/Likes Reference/$key")
        reference.removeValue().addOnSuccessListener {
            Log.d("FirebasePostRetrieverManager", "Post unliked successfully")
        }.addOnFailureListener {
            Log.e("FirebasePostRetrieverManager", "Failed to unlike post")
        }
    }

    private fun retrieveSavedPostsReference(callback: (List<LikeReference>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("users/$uid/Likes Reference")
        val referenceList = mutableListOf<LikeReference>()

        reference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val countDownLatch = CountDownLatch(snapshot.childrenCount.toInt())
                for(referenceSnapshot in snapshot.children){
                    val pid = referenceSnapshot.child("Post Id").getValue(String::class.java)
                    val uid = referenceSnapshot.child("User Id").getValue(String::class.java)
                    val key = referenceSnapshot.child("Key").getValue(String::class.java)
                    val likeReference = LikeReference(uid , pid , key)
                    referenceList.add(likeReference)
                    countDownLatch.countDown()
                    if (countDownLatch.count == 0L){
                        callback(referenceList)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
                Log.e("FirebasePostRetrieverManager", "Failed to retrieve posts", error.toException())
            }

        })

    }

    private fun retrieveHouseImages(uid: String , pid: String, callback: (List<Uri?>) -> Unit){
        val storage = FirebaseStorage.getInstance().reference


        val imagePaths = listOf(
            "${uid}/${pid}_images_0.jpg",
            "${uid}/${pid}_images_1.jpg",
            "${uid}/${pid}_images_2.jpg")


        val imagesFiles = mutableListOf<File>()
        val imagesUri = mutableListOf<Uri>()
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

    private fun userName(uid : String , callback : (String)-> Unit){
       val nameReference = FirebaseDatabase.getInstance().getReference("users/$uid")
        nameReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("name").getValue(String::class.java)
                if (userName != null) {
                    callback(userName)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null.toString())
            }

        })
    }
}