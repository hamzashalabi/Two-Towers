package eu.application.twotowers.create

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.FirebaseAuth


class FirebaseDatabaseManager (auth: FirebaseAuth): DatabaseManager{
    private val failure : Boolean = false
    private val success : Boolean = true
    private val user = auth.currentUser
    private val postsRef = FirebaseDatabase.getInstance().getReference("users")
    private val postRef = postsRef.child("${user?.uid}").child("posts")
    private val postId = postRef.push().key
    override fun savePostData(
        city: String,
        property: String,
        numRooms: String,
        numBathrooms: String,
        price: String,
        area: String,
        description: String,
        postStatus: String,
        callBack : (success : Boolean)-> Unit
    ) {
        if(city.isNullOrEmpty() || property.isNullOrEmpty() || numRooms.isNullOrEmpty()
            || numBathrooms.isNullOrEmpty() || price.isNullOrEmpty() || area.isNullOrEmpty()
            || description.isNullOrEmpty()){
            callBack(failure)
        } else{
            callBack(success)
        val postData = mapOf(
            "City" to city,
            "Property Options" to property,
            "Number Of Rooms" to numRooms,
            "Number Of Bathrooms" to numBathrooms,
            "Price" to price,
            "Area" to area,
            "Description" to description,
            "Time Stamp" to System.currentTimeMillis(),
            "Post Status" to postStatus
        )
        if(postId != null){
           postRef.child(postId).setValue(postData)
        }

        }
    }

    override fun storeImages(imagesUri: List<Uri>){

        val reference = FirebaseStorage.getInstance().reference
        val userRef = reference.child("${user?.uid}")

        imagesUri.forEachIndexed { index,uri->
            val fileName = "${postId}_images_$index.jpg"
            val userPostRef = userRef.child(fileName)
            val uploadTask = userPostRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            Log.e("Tag","Success")
        }
        uploadTask.addOnFailureListener{
            Log.e("Tag","Failure")
        }
    }
    }


    fun getPostId():String?{
        return postId

    }

    fun getUserId():String?{
        return user?.uid
    }
}