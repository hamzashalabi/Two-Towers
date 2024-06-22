package eu.application.twotowers.map

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import eu.application.twotowers.R

class GoogleMapsRepository (private val activity: AppCompatActivity) {
    private lateinit var googleMap : GoogleMap

    fun initializeGoogleMap(onMapReady :(GoogleMap)->Unit){
        val supportMapFragment = activity.supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        supportMapFragment.getMapAsync { map->
            googleMap = map
            onMapReady(googleMap)
        }
    }
}