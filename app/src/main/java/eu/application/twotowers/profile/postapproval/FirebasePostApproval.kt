package eu.application.twotowers.profile.postapproval

interface FirebasePostApproval {
    fun retrieveAllPosts(callback : (List<PostInfo?>)-> Unit)
    fun postApproval(approvalResult : ApprovalResult)
    fun updateWarning(approvalResult: ApprovalResult)
}