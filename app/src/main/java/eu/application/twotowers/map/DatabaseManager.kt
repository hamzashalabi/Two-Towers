package eu.application.twotowers.map

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

interface DatabaseManager {
    fun saveLocationToDatabase(location: LatLng , postId : String , userId : String)
    fun retrieveLocation(callback: (List<MapPost?>)-> Unit)
    fun retrieveCurrentUserImage(callback:(Uri?)-> Unit)
    fun warningCount(callback : (Int)-> Unit)
}