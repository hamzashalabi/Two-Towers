package eu.application.twotowers.create

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

class GalleryHelper ( private val activity : Activity) {

    companion object{
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type="image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        activity.startActivityForResult(intent , PICK_IMAGE_REQUEST_CODE )
    }

    fun handleActivityResult (requestCode : Int , resultCode : Int , data : Intent?,callback : GalleryCallback){
        if(requestCode == PICK_IMAGE_REQUEST_CODE)
            if(resultCode == Activity.RESULT_OK && data != null){
                val selectedImagesUri = data.clipData

                selectedImagesUri?.let{
                    callback.onImageSelected(it.toString())
                }
            }
        else {
            callback.onGalleryCanceled()
        }
    }
    interface GalleryCallback {
        fun onImageSelected(imageUri : String)
        fun onGalleryCanceled()
    }
}