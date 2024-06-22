package eu.application.twotowers.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MapViewModelFactory ( private val googleMapsRepository: GoogleMapsRepository ,
                            private val  databaseLocationManager: FirebaseDatabaseLocationManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create (modelClass: Class<T>):T{
        if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            return MapViewModel(googleMapsRepository , databaseLocationManager) as T
        }
        throw IllegalArgumentException ("Unknown ViewModel Class")
    }
}