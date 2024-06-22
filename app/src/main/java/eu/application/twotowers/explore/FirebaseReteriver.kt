package eu.application.twotowers.explore

import android.net.Uri

interface FirebaseReteriver {
    fun retrievePostInfo(callback:(MutableList<PostInfo?>)->Unit)
    fun retrieveCurrentUserImage(callback:(Uri?)-> Unit)
    fun likedPost(reference: LikeReference , callback: (success: Boolean) -> Unit)
}