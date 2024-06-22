package eu.application.twotowers.profile.feedback

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.application.twotowers.databinding.MessageToRowFeedbackBinding
import eu.application.twotowers.databinding.MessageToRowFeedbackBindingImpl

class SendFeedbackRecycleView (private val context : Context): RecyclerView.Adapter<SendFeedbackRecycleView.SendFeedbackHolder>() {

    var feedbackList :List<Feedback> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SendFeedbackHolder {
        val inflater = LayoutInflater.from(context)
        val binding = MessageToRowFeedbackBindingImpl.inflate(inflater , parent , false)
        return SendFeedbackHolder(binding)
    }

    override fun getItemCount(): Int {
        return feedbackList.size
    }

    override fun onBindViewHolder(holder: SendFeedbackHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.bind(feedback)
    }


    inner class SendFeedbackHolder (private val binding: MessageToRowFeedbackBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(feedback: Feedback){
            binding.feedback = feedback
            binding.executePendingBindings()
        }
    }

}