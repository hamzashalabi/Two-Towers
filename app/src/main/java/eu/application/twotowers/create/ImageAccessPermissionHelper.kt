package eu.application.twotowers.create

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class ImageAccessPermissionHelper(private val context: Context) {

    private val REQUEST_PERMISSION_CODE = 2

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestAccessPermission33(onPermissionGranted: () -> Unit) {
            requestPermission(Manifest.permission.READ_MEDIA_IMAGES, onPermissionGranted)
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun requestAccessPermission34(onPermissionGranted: () -> Unit) {
        requestPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, onPermissionGranted)
    }

    fun requestAccessPermission32(onPermissionGranted: () -> Unit) {
        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, onPermissionGranted)
    }

    private fun requestPermission(permission: String, onPermissionGranted: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as AppCompatActivity,
                arrayOf(permission),
                REQUEST_PERMISSION_CODE
            )
        } else {
            onPermissionGranted()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, onPermissionGranted: () -> Unit) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                onPermissionGranted()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun hasAccessPermission33(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun hasAccessPermission34(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
    }

    fun hasAccessPermission32(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}
