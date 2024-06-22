package eu.application.twotowers.map

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.R
import eu.application.twotowers.chat.ChatListActivity
import eu.application.twotowers.create.CreatePostActivity
import eu.application.twotowers.databinding.MapBinding
import eu.application.twotowers.databinding.MapBindingImpl
import eu.application.twotowers.explore.ExploreActivity
import eu.application.twotowers.profile.userprofile.ProfileActivity
import eu.application.twotowers.registration.LoginActivity
import kotlin.concurrent.thread

class MapActivity : AppCompatActivity() {

    private lateinit var viewModel: MapViewModel
    private lateinit var binding : MapBinding
    private lateinit var locationPermissionsHelper: LocationPermissionHelper
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var listOfMapPost: List<MapPost?>
    private val currentUser = FirebaseAuth.getInstance()
    private var postId : String? = null
    private var userId : String? = null

    companion object{
        const val USER_NAME_KEY = "userName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("map activity","on create")


        Places.initialize(applicationContext, getString(R.string.API_KEY))

        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID , Place.Field.ADDRESS
            , Place.Field.NAME , Place.Field.LAT_LNG))


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermissionsHelper = LocationPermissionHelper(this)


        val databaseLocationManager= FirebaseDatabaseLocationManager()
        val googleMapsRepository = GoogleMapsRepository(this)
        val viewModelFactory = MapViewModelFactory(googleMapsRepository , databaseLocationManager)
        viewModel = ViewModelProvider(this , viewModelFactory)[MapViewModel::class.java]
        binding.viewModel=viewModel

        viewModel.onCreate{
            if (locationPermissionsHelper.hasLocationPermission()) {
                getLastKnownLocationAndInitializeMap(false)
            } else {
                locationPermissionsHelper.requestLocationPermission {
                    getLastKnownLocationAndInitializeMap(false)
                }
            }
        }



        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@MapActivity, "An Error Appeared $p0", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng!!
                viewModel.zoomOnSelectedPlace(latLng)

            }

        })

        binding.CreateBefore.setOnClickListener{
            nav(this , CreatePostActivity::class.java)
        }

        binding.ExploreBefore.setOnClickListener {
            nav(this , ExploreActivity::class.java)
        }

        binding.ChatBefore.setOnClickListener {
            nav(this , ChatListActivity::class.java)
        }

        binding.ProfilePic.setOnClickListener {
            nav(this , ProfileActivity::class.java)
        }
        viewModel.retrievePost{postList ->
            listOfMapPost = postList
            for (mapPost in listOfMapPost){
                viewModel.addPinToMap(mapPost?.location!!)
            }

            viewModel.setOnMarkerClickListener { marker ->
                for (mapPost in listOfMapPost) {
                    if (marker.position == mapPost?.location) {
                        postDialog(this, mapPost)
                    }
                }
                true
            }
        }


        viewModel.warningCount()

        viewModel.warningCount.observe(this){warningCount->
            if (warningCount > 2){
                deleteDialog(this)
            }
        }



        var pinAdded = false
        postId = intent.getStringExtra("postKey")
        userId = intent.getStringExtra("userKey")

        viewModel.onCreate {
        if (postId != null && userId != null) {
            visible("Visible") // Make elements visible when the user reaches the screen
            val googleMap = viewModel.googleMap
                binding.addLocation.isEnabled = true
                binding.addLocation.setOnClickListener {
                    if (!pinAdded) {
                        binding.addLocation.isEnabled = false
                        binding.currentLocation.isEnabled= false
                        googleMap!!.setOnMapClickListener { latLng ->
                            viewModel.addPinToLocation(latLng, postId!!, userId!!)
                            binding.addLocation.visibility = View.INVISIBLE // Hide the "Add Location" button after adding the pin
                            // Set the visibility of other elements to INVISIBLE
                            visible("Invisible")
                            pinAdded = true
                            Toast.makeText(this, "post is Published", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            binding.currentLocation.isEnabled = true
            binding.currentLocation.setOnClickListener {
                   binding.currentLocation.isEnabled = false
                if (locationPermissionsHelper.hasLocationPermission()) {
                    getLastKnownLocationAndInitializeMap(true)
                    visible("Invisible")
                    Toast.makeText(this, "post is Published", Toast.LENGTH_SHORT).show()
                } else {
                    locationPermissionsHelper.requestLocationPermission {
                        getLastKnownLocationAndInitializeMap(true)
                        visible("Invisible")
                        Toast.makeText(this, "post is Published", Toast.LENGTH_SHORT).show()

                    }
                }

            }

        }
        }

        viewModel.retrieveCurrentUserImage { userImage->
            Glide.with(this).load(userImage).into(binding.ProfilePic)
        }

    }
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            locationPermissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults){

            }
        }



    private fun getLastKnownLocationAndInitializeMap(addPin: Boolean) {
        if (locationPermissionsHelper.hasLocationPermission()) {
            try {
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            initializeMapWithLocation(it,addPin)
                        }?: run {
                            // If lastLocation is null, request a new location update
                            fusedLocationProviderClient.requestLocationUpdates(
                                LocationRequest.create(),
                                locationCallback,
                                Looper.getMainLooper()
                            )
                        }
                    }
            } catch (e: SecurityException) {
                // Handle the case where permissions are not granted
                Log.e("TAG", "SecurityException: ${e.message}")
            }
        }
    }

    private fun initializeMapWithLocation(location: Location , addPin : Boolean) {
                val latLng = LatLng(location.latitude, location.longitude)
                viewModel.zoomOnSelectedPlace(latLng)

                if (addPin) {
                    viewModel.addPinToMap(latLng)
                    viewModel.addPinToLocation(latLng , postId!! , userId!!)
                }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                initializeMapWithLocation(location , false)
                // Remove the location callback after getting a location update
                fusedLocationProviderClient.removeLocationUpdates(this)
                break
            }
        }
    }

    private fun visible(visible : String){
        if(visible == "Visible"){
            binding.NavPar.visibility = View.INVISIBLE
            binding.MapAfter.visibility = View.INVISIBLE
            binding.CreateBefore.visibility = View.INVISIBLE
            binding.ExploreBefore.visibility = View.INVISIBLE
            binding.ChatBefore.visibility = View.INVISIBLE
            binding.ProfilePic.visibility = View.INVISIBLE
            binding.ProfileText.visibility = View.INVISIBLE

            binding.currentLocation.visibility = View.VISIBLE
            binding.addLocation.visibility = View.VISIBLE

        }else if(visible == "Invisible"){

            binding.NavPar.visibility = View.VISIBLE
            binding.MapAfter.visibility = View.VISIBLE
            binding.CreateBefore.visibility = View.VISIBLE
            binding.ExploreBefore.visibility = View.VISIBLE
            binding.ChatBefore.visibility = View.VISIBLE
            binding.ProfilePic.visibility = View.VISIBLE
            binding.ProfileText.visibility = View.VISIBLE

            binding.currentLocation.visibility = View.INVISIBLE
            binding.addLocation.visibility = View.INVISIBLE
        }
    }

    private fun nav(from : Activity, to : Class<out Activity>){
        val intent = Intent(from , to)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        from.startActivity(intent)
    }

    private fun postDialog(context : Context, mapPost: MapPost?){
        val builder = Builder(context)

        val inflater = LayoutInflater.from(context)
        val customView = inflater.inflate(R.layout.map_post , null)

        val viewPager = customView.findViewById<ViewPager>(R.id.imageCarousel)

        val adapter = ImagePagerAdapter(context , mapPost!!.houseImages)
        viewPager.adapter = adapter

        customView.findViewById<TextView>(R.id.description).text = mapPost.description

        customView.setOnClickListener{
            val intent = Intent(this , ExploreActivity::class.java)
            intent.putExtra(USER_NAME_KEY ,mapPost.pId)
            startActivity(intent)
        }

        builder.setView(customView)
        val dialog =builder.create()
        dialog.show()
    }

    private fun deleteDialog(context: Context){
        val builder = Builder(context)
        builder.setTitle("Content Violation")
        builder.setMessage("Your account was deleted for continuous violation of our community guidelines.")

        builder.setPositiveButton("ok"){dialog,_->
            dialog.dismiss()
            currentUser.signOut()
            val intent = Intent(context , LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
