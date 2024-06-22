package eu.application.twotowers.chat

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.application.twotowers.R
import eu.application.twotowers.databinding.ChatRowListBinding

class ChatListRecycleView(private val context : Context): RecyclerView.Adapter<ChatListRecycleView.ChatListHolder>() {

    var userList : List<User?> = emptyList()
        set(value) {
            field = value
            if(bothListsUpdated()){
                notifyDataSetChanged()
            }
        }

    var latestMessage : List<Message?> = emptyList()
        set(value) {
            field = value
            if (bothListsUpdated()){
                notifyDataSetChanged()
            }
        }


    companion object{
        const val USER_NAME = "user_name"
        const val IMAGE_KEY = "image key"
        const val USER_UID = "uid"
    }

    private fun bothListsUpdated() : Boolean{
        return userList.isNotEmpty() && latestMessage.isNotEmpty()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ChatRowListBinding.inflate(inflater , parent , false)
        return ChatListHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ChatListHolder, position: Int) {
        val user = userList[position]
        val hasMessages = latestMessage.any { message ->
            message?.fromId == user?.uId || message?.toId == user?.uId
        }
        if(hasMessages){
            val message = latestMessage.firstOrNull{
                it?.fromId == user?.uId || it?.toId == user?.uId
            }
            holder.bindUser(user)
            holder.bindMessage(message)
        }
    }

    inner class ChatListHolder(private val binding : ChatRowListBinding):RecyclerView.ViewHolder(binding.root) {

        fun bindUser (user: User?){

            if(user?.userImage != null)
                Glide.with(context).load(user.userImage).into(binding.userImageView)
            else
                Glide.with(context).load(R.drawable.def_profile_pic).into(binding.userImageView)

            binding.userNameTextView.text = user?.userName

            binding.RowList.setOnClickListener {
                val intent = Intent(context , ChatActivity::class.java)
                intent.putExtra(USER_NAME,user?.userName)
                intent.putExtra(IMAGE_KEY,user?.userImage.toString())
                intent.putExtra(USER_UID ,user?.uId)
                context.startActivity(intent)
            }
            binding.executePendingBindings()
        }

        fun bindMessage(message: Message?){
            binding.latestMessage = message
            binding.executePendingBindings()
        }

    }
}