package eu.application.twotowers.profile.posts

import android.net.Uri

data class LikedPostInfo(val description : String? = null, val timestamp: String? = null,
                         val city : String? = null, val area : String? = null,
                         val numRooms : String? = null, val numBathrooms : String? = null,
                         val price :String? = null, val propertyOptions :String? = null,
                         val houseImages : List<Uri?>? = null, val userName : String? = null,
                         val userImage : Uri? = null , val key : String? = null)
