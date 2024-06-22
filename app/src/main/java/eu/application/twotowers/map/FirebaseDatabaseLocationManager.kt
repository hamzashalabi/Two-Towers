package eu.application.twotowers.map

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.concurrent.CountDownLatch

class FirebaseDatabaseLocationManager : DatabaseManager {

    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    override fun saveLocationToDatabase(location: LatLng , postId : String , userId :String) {

        val parentRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val childRef = parentRef.child("posts").child(postId)

        val locationData = mapOf(
            "Lat" to location.latitude,
            "Lng" to location.longitude
        )

        childRef.updateChildren(locationData)

    }

    override fun retrieveLocation(callback: (List<MapPost?>) -> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("users")
        val postList = mutableListOf<MapPost?>()

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val countDownLatch = CountDownLatch(snapshot.childrenCount.toInt())
                Log.e("count down latch","${countDownLatch.count}")
                for (userSnapshot in snapshot.children){
                    val uId = userSnapshot.key
                    val postReference = userSnapshot.child("posts")
                    countDownLatch.countDown()

                    for (postSnapshot in postReference.children) {
                        val pId = postSnapshot.key
                        val postStatus = postSnapshot.child("Post Status").getValue(String::class.java)
                        if(postStatus == "pending"){
                            Log.e("map post","pending")
                            continue
                        }
                        val description = postSnapshot.child("Description").getValue(String::class.java)
                        val lat = postSnapshot.child("Lat").getValue(Double::class.java)
                        val lng = postSnapshot.child("Lng").getValue(Double::class.java)

                        if (lat != null && lng != null) {
                            val location = LatLng(lat, lng)
                            retrieveHouseImages(uId, pId) { houseImages ->
                                val post = MapPost(location, description, houseImages, pId)
                                postList.add(post)
                                Log.e("post", "$post")
                                if (countDownLatch.count == 0L) {
                                    callback(postList)
                                    Log.e("post list in firebase","$postList")
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
                Log.e("retrieve location","$error")
            }

        })
    }

    override fun retrieveCurrentUserImage(callback: (Uri?) -> Unit) {
        val reference = FirebaseStorage.getInstance().reference

        val path = "$currentUser/userImage.jpg"
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

    override fun warningCount(callback: (Int)-> Unit) {
        val reference = FirebaseDatabase.getInstance().getReference("users/${currentUser}")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userWarningCount = snapshot.child("warning").getValue(Int::class.java)
                if (userWarningCount != null){
                    callback(userWarningCount)

                if (userWarningCount > 2){
                    reference.removeValue()
                    FirebaseAuth.getInstance().currentUser?.delete()
                }
            }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("user warning count ","$error")
            }

        })
    }

    private fun retrieveHouseImages(uId : String? , pId :String? , callback: (List<Uri>) -> Unit){
        val storage = FirebaseStorage.getInstance().reference


        val imagePaths = listOf(
            "${uId}/${pId}_images_0.jpg",
            "${uId}/${pId}_images_1.jpg",
            "${uId}/${pId}_images_2.jpg")


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
}