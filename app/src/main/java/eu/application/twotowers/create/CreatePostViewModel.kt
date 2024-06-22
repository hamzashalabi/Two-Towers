package eu.application.twotowers.create


import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreatePostViewModel ( private val galleryHelper: GalleryHelper ,
                            private val databaseManager : DatabaseManager) : ViewModel() {

    val city = MutableLiveData<String>()

    val property = MutableLiveData<String>()

    val numRooms = MutableLiveData<String>()

    val numBathrooms = MutableLiveData<String>()

    val price = MutableLiveData<String>()

    val area = MutableLiveData<String>()

    val description = MutableLiveData<String>()

    private val _postStatus = MutableLiveData<PostStatus>()
    val postStatus :LiveData<PostStatus> = _postStatus

    fun onPublishClicked(){
        val city = city.value ?: return
        val property = property.value ?: return
        val numRooms = numRooms.value ?: return
        val numBathrooms = numBathrooms.value ?: return
        val price = price.value ?: return
        val area = area.value ?: return
        val description = description.value ?: return
        val postStatus = "pending"
        databaseManager.savePostData(city,property,numRooms,numBathrooms,price,area,description,postStatus){success ->
          _postStatus.value = if(success) PostStatus.SUCCESS else PostStatus.FAILURE
        }
    }

    fun openGallery(){
        galleryHelper.openGallery()
    }

    fun handleGalleryResult(requestCode : Int , resultCode : Int , data : Intent? , callback: GalleryHelper.GalleryCallback){
        galleryHelper.handleActivityResult(requestCode ,resultCode , data , callback)
    }

    fun storeImages(imagesUri : List<Uri>){
        databaseManager.storeImages(imagesUri)
    }


}