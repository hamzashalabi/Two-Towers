package eu.application.twotowers.profile.posts

interface FirebasePostRetriever {
    fun retrieveMyPosts(callback:(List<PostInfo?>)->Unit)
    fun retrieveSavedPosts(callback:(List<LikedPostInfo?>)->Unit)
    fun unlikePost(key : String)
    fun deletePost(pId:String)
}