package eu.application.twotowers.profile.feedback

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import eu.application.twotowers.databinding.SendFeedbackBinding
import eu.application.twotowers.databinding.SendFeedbackBindingImpl

class SendFeedbackActivity :AppCompatActivity(){

    private lateinit var binding : SendFeedbackBinding
    private lateinit var viewModel : FeedbackViewModel
    private lateinit var sendFeedbackRecycleView: SendFeedbackRecycleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SendFeedbackBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseFeedbackManager = FirebaseFeedbackManager()
        val feedbackViewModelFactory = FeedbackViewModelFactory(firebaseFeedbackManager)
        viewModel = ViewModelProvider(this, feedbackViewModelFactory)[FeedbackViewModel::class.java]

        sendFeedbackRecycleView = SendFeedbackRecycleView(this)
        binding.messageRecycleView.adapter = sendFeedbackRecycleView
        binding.messageRecycleView.layoutManager = LinearLayoutManager(this)


        binding.sendButton.setOnClickListener {
            val feedbackMessage = binding.writeMessageEditText.text.toString()
            val userName = intent.getStringExtra("userName")
            val feedback = Feedback(feedbackMessage ,userName )
            viewModel.sendFeedback(feedback)

            binding.writeMessageEditText.text.clear()
        }

        viewModel.retrieveFeedbackUser { feedbackList->
            sendFeedbackRecycleView.feedbackList = emptyList()
            sendFeedbackRecycleView.feedbackList = feedbackList
        }

        viewModel.feedbackStatus.observe(this){status ->
            when(status){
                FeedbackStatus.SUCCESS -> {
                    Log.e("feedback activity ","success")
                    binding.messageRecycleView.scrollToPosition((binding.messageRecycleView.adapter as SendFeedbackRecycleView).itemCount - 1)
                }
                FeedbackStatus.FAILURE -> {
                    Log.e("feedback activity ","failure")
                }
            }
        }

        binding.sendFeedbackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

    }
}