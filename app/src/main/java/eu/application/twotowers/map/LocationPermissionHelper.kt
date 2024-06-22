package eu.application.twotowers.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class LocationPermissionHelper (private val context : Context) {

    private val REQUEST_PERMISSION_CODE = 1

    fun requestLocationPermission(onPermissionGranted : ()->Unit){
        if(ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED
            ||
            ActivityCompat.checkSelfPermission(
                context ,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED
            )
            ActivityCompat.requestPermissions(
                context as AppCompatActivity ,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSION_CODE
            )
        else
            onPermissionGranted()
    }

    fun onRequestPermissionsResult(requestCode :Int , permissions : Array<String> ,
                                  grantResults : IntArray , onPermissionGranted: () -> Unit ){
        if(requestCode == REQUEST_PERMISSION_CODE){
            if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED){
                onPermissionGranted()
            }
        }
    }

    fun hasLocationPermission() : Boolean{
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    }
}