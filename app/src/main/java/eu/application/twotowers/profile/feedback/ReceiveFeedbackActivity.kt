package eu.application.twotowers.profile.feedback

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.application.twotowers.databinding.ReceiveFeedbackActivityBinding
import eu.application.twotowers.databinding.ReceiveFeedbackActivityBindingImpl

class ReceiveFeedbackActivity :AppCompatActivity(){

    private lateinit var binding : ReceiveFeedbackActivityBinding
    private lateinit var viewModel : FeedbackViewModel
    private lateinit var receiveFeedbackRecycleView: ReceiveFeedbackRecycleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReceiveFeedbackActivityBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseFeedbackManager = FirebaseFeedbackManager()
        val feedbackViewModelFactory = FeedbackViewModelFactory(firebaseFeedbackManager)
        viewModel = ViewModelProvider(this , feedbackViewModelFactory)[FeedbackViewModel::class.java]

        binding.feedbackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        receiveFeedbackRecycleView = ReceiveFeedbackRecycleView(this)
        binding.receiveFeedbackRecycleView.adapter = receiveFeedbackRecycleView
        binding.receiveFeedbackRecycleView.layoutManager = LinearLayoutManager(this)

        viewModel.retrieveFeedback { feedbackList->
            receiveFeedbackRecycleView.feedbackList = feedbackList
        }

    }
}