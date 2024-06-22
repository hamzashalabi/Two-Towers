package eu.application.twotowers.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import eu.application.twotowers.chat.ChatActivity.ToIdHolder.toId
import eu.application.twotowers.databinding.ChatBinding
import eu.application.twotowers.databinding.ChatBindingImpl
import eu.application.twotowers.explore.PostRecycleView

class ChatActivity : AppCompatActivity() {

    private lateinit var binding : ChatBinding
    private lateinit var viewModel : ChatViewModel
    private lateinit var chatRecycleView : ChatRecycleView

    object ToIdHolder{
        var toId : String? = null
        var fromId : String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatBindingImpl.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseDatabaseReteriver = FirebaseDatabaseReteriver()
        val viewModelFactory = ChatViewModelFactory(firebaseDatabaseReteriver)
        viewModel = ViewModelProvider(this , viewModelFactory)[ChatViewModel::class.java]

        binding.chatViewModel = viewModel

        val fromID = FirebaseAuth.getInstance().uid

        val userName = intent.getStringExtra(ChatListRecycleView.USER_NAME)
        val userImage = intent.getStringExtra(ChatListRecycleView.IMAGE_KEY)
        val toIdL = intent.getStringExtra(ChatListRecycleView.USER_UID)

        val userNameA = intent.getStringExtra(AddChatRecycleView.USER_NAME_ADD)
        val userImageA = intent.getStringExtra(AddChatRecycleView.IMAGE_KEY_ADD)
        val toIdA = intent.getStringExtra(AddChatRecycleView.UID_ADD)

        val userNameP = intent.getStringExtra(PostRecycleView.USER_NAME_POST)
        val userImageP = intent.getStringExtra(PostRecycleView.USER_IMAGE_POST)
        val toIdP = intent.getStringExtra(PostRecycleView.USER_TO_ID)



        if(userNameA != null && userImageA != null && toIdA != null){
            toId=toIdA
            ToIdHolder.fromId=fromID
            Glide.with(this).load(userImageA).into(binding.userImageView)
            binding.userNameTextView.text = userNameA

        }else if(userName != null && userImage != null && toId != null){
            toId = toIdL
            ToIdHolder.fromId = fromID
            Glide.with(this).load(userImage).into(binding.userImageView)
            binding.userNameTextView.text = userName
        }else {
            toId = toIdP
            ToIdHolder.fromId = fromID
            Glide.with(this).load(userImageP).into(binding.userImageView)
            binding.userNameTextView.text = userNameP
        }


        binding.backButton.setOnClickListener {
            val intent = Intent(this , ChatListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }



        chatRecycleView = ChatRecycleView(this)
        binding.messageRecycleView.adapter = chatRecycleView
        binding.messageRecycleView.layoutManager = LinearLayoutManager(this)


        val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (chatRecycleView.itemCount>0) {
                    binding.messageRecycleView.scrollToPosition(chatRecycleView.itemCount - 1)
                    binding.messageRecycleView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                }
            }

        }
        binding.messageRecycleView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)


        binding.messageRecycleView.scrollToPosition((binding.messageRecycleView.adapter as ChatRecycleView).itemCount-1)


        binding.sendButton.setOnClickListener {
        val messageText = binding.writeMessageEditText.text.toString()

        val message = Message(messageText , fromID , toId)

        binding.writeMessageEditText.text.clear()
            viewModel.saveUserMessages(message)
        }

        viewModel.retrieveUserMessages { messagesList ->
            chatRecycleView.messageList = messagesList
            chatRecycleView.notifyDataSetChanged()
            Log.e("retrieve activity","$messagesList")
        }

        viewModel.messageStatus.observe(this){status->
            when (status){
                MessageStatus.SUCCESS->{
                    Log.e("message activity ","success")
                    binding.messageRecycleView.scrollToPosition((binding.messageRecycleView.adapter as ChatRecycleView).itemCount-1)
                }
                MessageStatus.FAILURE->{
                    Log.e("message activity ","failure")
                }
            }
        }

    }
}