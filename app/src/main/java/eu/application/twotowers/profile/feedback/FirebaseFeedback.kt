package eu.application.twotowers.profile.feedback

interface FirebaseFeedback {
    fun sendFeedback(feedback: Feedback , callback : (success : Boolean) -> Unit)
    fun retrieveFeedbackAdmin(callback : (List<Feedback>) -> Unit)
    fun retrieveFeedbackUser(callback : (List<Feedback>) -> Unit)
}