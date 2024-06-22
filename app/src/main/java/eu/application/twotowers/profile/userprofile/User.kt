package eu.application.twotowers.profile.userprofile

import android.net.Uri

data class User(val userName : String? = null , val userImage : Uri? = null,
                val userRole : String? = null , val warning : Int? = null)
