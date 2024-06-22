package eu.application.twotowers.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.application.twotowers.R
import eu.application.twotowers.databinding.ChatRowAddBinding

class AddChatRecycleView (private val context : Context): RecyclerView.Adapter<AddChatRecycleView.AddChatHolder>(){

    var userList : List<User?> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object{
        const val USER_NAME_ADD = "user_name"
        const val IMAGE_KEY_ADD = "image_key"
        const val UID_ADD = "uid"
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddChatRecycleView.AddChatHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ChatRowAddBinding.inflate(inflater , parent , false)
        return AddChatHolder(binding)
    }

    override fun onBindViewHolder(holder: AddChatRecycleView.AddChatHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun searchUser(searchList : List<User?>){
        userList = searchList
        notifyDataSetChanged()
    }

    inner class AddChatHolder (private val binding : ChatRowAddBinding):RecyclerView.ViewHolder(binding.root){

        fun bind (user : User?){
            binding.chatRow = user
            if(user?.userImage != null)
            Glide.with(context).load(user.userImage).into(binding.userImageView)
            else
                Glide.with(context).load(R.drawable.def_profile_pic).into(binding.userImageView)

            binding.RowAdd.setOnClickListener {
                val intent = Intent(context , ChatActivity::class.java)
                intent.putExtra(USER_NAME_ADD,user?.userName)
                intent.putExtra(IMAGE_KEY_ADD,user?.userImage.toString())
                intent.putExtra(UID_ADD,user?.uId)
                context.startActivity(intent)
            }

            binding.executePendingBindings()
        }

    }
}