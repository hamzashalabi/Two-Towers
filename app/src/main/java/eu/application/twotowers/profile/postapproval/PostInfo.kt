package eu.application.twotowers.profile.postapproval

import android.net.Uri

data class PostInfo(val description : String? = null, val timestamp: String? = null,
                    val area : String? = null, val price :String? = null,
                    val userName : String? = null, val houseImages : List<Uri?>? = null,
                    val userImage : Uri? =null , val uid : String? = null , val pid : String? = null)
