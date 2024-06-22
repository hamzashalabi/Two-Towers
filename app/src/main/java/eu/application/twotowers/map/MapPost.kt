package eu.application.twotowers.map

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

data class MapPost(val location : LatLng? = null , val description : String? = null ,
                   val houseImages : List<Uri?> , val pId : String? = null )
