package eu.application.twotowers.map

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapViewModel (private val googleMapsRepository: GoogleMapsRepository ,
                    private val databaseLocationManager: DatabaseManager): ViewModel() {

              var googleMap: GoogleMap? = null
    private val _postList = MutableLiveData<List<MapPost?>>()


    private val _warningCount = MutableLiveData<Int>()
    val warningCount : LiveData<Int> = _warningCount

    fun onCreate(callback: (GoogleMap?) -> Unit) {
        googleMapsRepository.initializeGoogleMap {
            googleMap = it
            callback(it)
            Log.e("map activity view model","view model on create")
        } }



    fun zoomOnSelectedPlace(latLng: LatLng){
        if(googleMap != null) {
            val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            googleMap!!.animateCamera(newLatLngZoom)
        }
    }

    fun addPinToLocation(latLng: LatLng , postId : String , userId : String){
          googleMap!!.addMarker(MarkerOptions().position(latLng))
        databaseLocationManager.saveLocationToDatabase(latLng , postId , userId)
    }

    fun retrievePost(callback: (List<MapPost?>) -> Unit){
        databaseLocationManager.retrieveLocation { postList ->
            _postList.postValue(postList)
            callback(postList)
        }
    }

    fun addPinToMap (latLng: LatLng){
        googleMap!!.addMarker(MarkerOptions().position(latLng))
    }

    fun setOnMarkerClickListener(listener : GoogleMap.OnMarkerClickListener){
        googleMap!!.setOnMarkerClickListener(listener)
    }


    fun retrieveCurrentUserImage(callback : (Uri?) -> Unit){
        databaseLocationManager.retrieveCurrentUserImage {userImage->
            callback(userImage)
        }
    }

    fun warningCount(){
        databaseLocationManager.warningCount { warning ->
            _warningCount.postValue(warning)
        }
    }
}

