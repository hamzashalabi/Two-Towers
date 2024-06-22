package eu.application.twotowers.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.databinding.MessageFromRowBinding
import eu.application.twotowers.databinding.MessageFromRowBindingImpl
import eu.application.twotowers.databinding.MessageToRowBinding
import eu.application.twotowers.databinding.MessageToRowBindingImpl

class ChatRecycleView(private val context : Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messageList = mutableListOf<Message?>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val LEFT_MESSAGE = 0
        private const val RIGHT_MESSAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when(viewType){
            LEFT_MESSAGE->{
            val binding = MessageToRowBindingImpl.inflate(inflater, parent ,false)
            ToChatHolder(binding)
            }
            RIGHT_MESSAGE->{val binding = MessageFromRowBindingImpl.inflate(inflater , parent , false)
                FromChatHolder(binding)}
            else -> throw IllegalArgumentException("invalid")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]

        when (holder){
            is FromChatHolder -> holder.bind(message!!)
            is ToChatHolder -> holder.bind(message!!)
            }
        }

    override fun getItemCount(): Int {
       return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message?.fromId == FirebaseAuth.getInstance().uid){
            Log.e("tag","${message?.fromId}")

            LEFT_MESSAGE }
            else
            RIGHT_MESSAGE

    }
    inner class FromChatHolder(private val binding : MessageFromRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message){
            binding.message = message
            binding.executePendingBindings()
        }

    }

    inner class ToChatHolder(private val binding : MessageToRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message){
            binding.message = message
            binding.executePendingBindings()
        }

    }
}

