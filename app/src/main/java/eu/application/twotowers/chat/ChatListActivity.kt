package eu.application.twotowers.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.create.CreatePostActivity
import eu.application.twotowers.databinding.ChatListBinding
import eu.application.twotowers.databinding.ChatListBindingImpl
import eu.application.twotowers.explore.ExploreActivity
import eu.application.twotowers.map.MapActivity
import eu.application.twotowers.profile.userprofile.ProfileActivity

class ChatListActivity :AppCompatActivity(){

    private lateinit var binding : ChatListBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var chatListRecycleView: ChatListRecycleView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatListBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseDatabaseReteriver = FirebaseDatabaseReteriver()
        val viewModelFactory = ChatViewModelFactory(firebaseDatabaseReteriver)
        viewModel = ViewModelProvider(this , viewModelFactory)[ChatViewModel::class.java]
        binding.chatListViewModel = viewModel

        chatListRecycleView = ChatListRecycleView(this)
        binding.chatListRecycleViewLayout.adapter = chatListRecycleView
        binding.chatListRecycleViewLayout.layoutManager = LinearLayoutManager(this)

        viewModel.updateUserInfoAndMessages { users, messages ->
            chatListRecycleView.userList = users
            chatListRecycleView.latestMessage = messages
        }

        binding.searchButton.setOnClickListener {
            val intent = Intent(this , AddChatActivity::class.java)
            startActivity(intent)
        }

        binding.CreateBefore.setOnClickListener {
            nav(this , CreatePostActivity::class.java)
        }

        binding.ExploreBefore.setOnClickListener {
            nav(this , ExploreActivity::class.java)
        }

        binding.MapAfter.setOnClickListener {
            nav(this , MapActivity::class.java)
        }

        binding.ProfilePic.setOnClickListener {
            nav(this , ProfileActivity::class.java)
        }

        viewModel.retrieveCurrentUserImage { userImage->
            Glide.with(this).load(userImage).into(binding.ProfilePic)
        }


        }

    private fun nav(from : Activity, to : Class<out Activity>){
        val intent = Intent(from , to)
        from.startActivity(intent)
    }
    }