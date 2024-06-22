package eu.application.twotowers.profile.posts

import androidx.lifecycle.ViewModel

class PostViewModel (private val firebasePostRetriever: FirebasePostRetriever): ViewModel() {

    fun myPosts(callback: (List<PostInfo?>) -> Unit){
        firebasePostRetriever.retrieveMyPosts { myPosts ->
            callback(myPosts)
        }
    }

    fun savedPosts(callback: (List<LikedPostInfo?>) -> Unit){
        firebasePostRetriever.retrieveSavedPosts { savedPosts ->
            callback(savedPosts)
        }
    }

    fun unlikePost(key : String){
        firebasePostRetriever.unlikePost(key)
    }

    fun deletePost(pId: String){
        firebasePostRetriever.deletePost(pId)
    }
}