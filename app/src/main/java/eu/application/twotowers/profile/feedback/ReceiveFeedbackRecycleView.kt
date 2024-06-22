package eu.application.twotowers.profile.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.application.twotowers.databinding.FeedbackRowBinding
import eu.application.twotowers.databinding.FeedbackRowBindingImpl

class ReceiveFeedbackRecycleView (private val context : Context): RecyclerView.Adapter<ReceiveFeedbackRecycleView.ReceiveFeedbackHolder>() {

    var feedbackList : List<Feedback> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiveFeedbackHolder {
        val inflater = LayoutInflater.from(context)
        val binding = FeedbackRowBindingImpl.inflate(inflater , parent , false)
        return ReceiveFeedbackHolder(binding)
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    override fun onBindViewHolder(holder: ReceiveFeedbackHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.bindFeedback(feedback)
    }

    inner class ReceiveFeedbackHolder(private val binding : FeedbackRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindFeedback(feedback: Feedback){
            binding.feedback = feedback
            Glide.with(context).load(feedback.userImage).into(binding.userImageViewFeedback)
            binding.executePendingBindings()
        }
    }
}