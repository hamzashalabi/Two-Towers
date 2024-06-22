package eu.application.twotowers.profile.postapproval

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch

@RequiresApi(Build.VERSION_CODES.O)
class FirebasePostApprovalManager : FirebasePostApproval {

    private var uid :String? = null
    private var warningCount : Int? = null
    val reference = FirebaseDatabase.getInstance().getReference().child("users")

    override fun retrieveAllPosts(callback: (List<PostInfo?>) -> Unit) {
        val postList = mutableListOf<PostInfo?>()

        val query = if(uid == null){
            reference.orderByKey()
        }else{
            reference.startAfter(uid)
        }

        query.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    uid = snapshot.key
                    val currentUid = snapshot.key
                    val userName = snapshot.child("name").getValue(String::class.java)
                    val postReference = snapshot.child("posts")

                    for (postSnapshot in postReference.children){
                        Log.e("post snapshot count","${postReference.children.count()}")
                        val postStatus = postSnapshot.child("Post Status").getValue(String::class.java)
                        Log.e("approval ","$postStatus")
                        if(postStatus == "pending"){
                            val pid = postSnapshot.key
                            val description = postSnapshot.child("Description").getValue(String::class.java)
                            val tempTimeStamp = postSnapshot.child("Time Stamp").getValue(Long::class.java)
                            val dateTime = LocalDateTime.ofInstant(tempTimeStamp?.let {
                                Instant.ofEpochMilli(
                                    it
                                )
                            }, ZoneId.of("Asia/Amman"))
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            val timestamp = formatter.format(dateTime)
                            val area = postSnapshot.child("Area").getValue(String::class.java)
                            val price = postSnapshot.child("Price").getValue(String::class.java)

                            retrievePostImages(currentUid.toString() , pid.toString()){houseImages->
                                retrieveCurrentUsersImages(currentUid.toString()){userImage->
                                    val post = PostInfo(description , timestamp , area , price ,userName, houseImages ,userImage, currentUid , pid)
                                    postList.add(post)
                                        callback(postList)
                                }
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
                Log.e("post approval retrieval","$error")
            }
        })
    }

    override fun postApproval(approvalResult: ApprovalResult) {
        val postReference = reference.child("${approvalResult.uid}/posts/${approvalResult.pid}/Post Status")

        postReference.setValue(approvalResult.approvalResult).addOnSuccessListener {
            Log.e("update post status ","success")
        }.addOnFailureListener{
            Log.e("update post status ","failure")
        }
    }

    override fun updateWarning(approvalResult: ApprovalResult) {
        val warnReference = reference.child("${approvalResult.uid}")
        Log.e("uid","${approvalResult.uid}")
        val warnReferenceUpdate = reference.child("${approvalResult.uid}/warning")


        if(approvalResult.warning == true){
        warnReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                warningCount = snapshot.child("warning").getValue(Int::class.java)
                Log.e("approval warning count ","$warningCount")
                if(warningCount != null){
                warningCount = warningCount!! + 1
                warnReferenceUpdate.setValue(warningCount).addOnSuccessListener {
                    Log.e("warning count success","$warningCount")
                }.addOnFailureListener{
                    Log.e("warning count failure","$warningCount")
                }
                }
                Log.e("approval warning count +1 ","$warningCount")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("warning count ","error")
            }

        })
        }

        val deletePost = reference.child("${approvalResult.uid}/posts/${approvalResult.pid}")
        deletePost.removeValue().addOnSuccessListener {
            Log.e("post deletion","success")
        }.addOnFailureListener{
            Log.e("post deletion","failure")
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
                countDownLatch.countDown()
                if(countDownLatch.count == 0L)
                    callback(imagesUri)

            }.addOnFailureListener {
                Log.e("Error", "Database Error image error")
                countDownLatch.countDown()
                if(countDownLatch.count == 0L)
                    callback(emptyList())

            }
        }
    }


    fun retrieveCurrentUsersImages(uid : String , callback: (Uri?) -> Unit) {
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