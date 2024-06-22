package eu.application.twotowers.profile.userprofile

interface UserProfileRetriever {
    fun retrieveUserProfile(callback: (User?) -> Unit)
}