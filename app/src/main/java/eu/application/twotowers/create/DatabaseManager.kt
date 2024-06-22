package eu.application.twotowers.create

import android.net.Uri

interface DatabaseManager {
    fun savePostData(
        city: String, property: String, numRooms: String,
        numBathrooms: String, price: String, area:String,
        description: String , postStatus :String, callBack:(success : Boolean) -> Unit
    )

    fun storeImages(imagesUri : List<Uri>)
}