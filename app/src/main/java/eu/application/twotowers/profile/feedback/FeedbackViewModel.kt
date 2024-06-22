package eu.application.twotowers.profile.feedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FeedbackViewModel(private val firebaseFeedback: FirebaseFeedback) : ViewModel() {

    private val _feedbackStatus = MutableLiveData<FeedbackStatus>()
    val feedbackStatus : LiveData<FeedbackStatus> = _feedbackStatus


    fun sendFeedback(feedback: Feedback){
        firebaseFeedback.sendFeedback(feedback){success->
            _feedbackStatus.value = if(success) FeedbackStatus.SUCCESS else FeedbackStatus.FAILURE
        }
    }

    fun retrieveFeedback(callback: (List<Feedback>) -> Unit){
        firebaseFeedback.retrieveFeedbackAdmin{feedbackList->
            callback(feedbackList)
        }
    }

    fun retrieveFeedbackUser(callback: (List<Feedback>) -> Unit){
        firebaseFeedback.retrieveFeedbackUser { feedbackList ->
            callback(feedbackList)
        }
    }
}