package eu.application.twotowers.explore

import android.net.Uri


data class PostInfo(val description : String? = null, val timestamp: String? = null,
                    val city : String? = null ,val area : String? = null ,
                    val numRooms : String? = null , val numBathrooms : String? = null ,
                    val price :String? = null , val propertyOptions :String? = null ,
                    val userName : String? = null, val houseImages : List<Uri?>? = null ,
                    val uid :String? = null , val pid : String? = null ,
                    val userImage : Uri? = null)